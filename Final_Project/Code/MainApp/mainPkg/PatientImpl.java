package mainAppPkg;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * 
 * Patient function implementation
 *
 */
@Path("/patient")
public class PatientImpl {

	// URL of the JMS server. DEFAULT_BROKER_URL will just mean
	// that JMS server is on localhost
	// default broker URL is : tcp://localhost:61616"
	private static     String url = ActiveMQConnection.DEFAULT_BROKER_URL;

	// Name of the queue we will receive messages from
	private static String email_subject = "EMAIL"; //Queue Name
	private static String analytics_subject = "ANALYTICS"; //Queue Name

	// Hazelcast instance
	static HazelcastInstance instance = Hazelcast.newHazelcastInstance();

	/**
	 * Fetch patient info
	 * @param pId
	 * @return
	 */
	@GET
	@Path("/GetPatientInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Patient getPatientInfo(@QueryParam("pId") String pId) {

		// Patient info
		Patient pObj = new Patient();

		// Fetch records from cache
		IMap<String, Map<String, Object>> patientsMap = instance.getMap("patients");
		IMap<String, Map<String, Object>> treatmentsMap = instance.getMap("treatment");

		// Fetch patient info from cache
		if(!fetchPatientInfoFromCache(patientsMap, treatmentsMap, pObj, pId)) {

			// Fetch patient info from database if not found in cache
			fetchPatientInfoFromDB(pObj, pId);
		}

		return pObj;		
	}

	/**
	 * Fetch patient info from cache map
	 * @param patientsMap
	 * @param pObj
	 * @param pId
	 * @return cacheHitFlag
	 */
	public boolean fetchPatientInfoFromCache(IMap<String, Map<String, Object>>  patientsMap, IMap<String, Map<String, Object>> treatmentsMap, Patient pObj, String pId) {

		// Iterate over list and find if queried record is present
		for(String pIdEntry : patientsMap.keySet()) {

			if(pId.equals(pIdEntry)) {

				Map<String, Object> patientInfoMap = patientsMap.get(pIdEntry);
				pObj.setPid(pId);
				pObj.setFirst_name((String)patientInfoMap.get("first_name"));
				pObj.setLast_name((String)patientInfoMap.get("last_name"));
				pObj.setEmail((String)patientInfoMap.get("email"));
				pObj.setAddress((String)patientInfoMap.get("address"));
				pObj.setPhone((String)patientInfoMap.get("phone"));

				Map<String, Object> patientTreatmentMap = treatmentsMap.get(pIdEntry);
				pObj.setTreatment((String)patientTreatmentMap.get("treatment"));
				pObj.setDiagnosis((String)patientTreatmentMap.get("diagnosis"));

				// True indicates cache hit
				System.out.println("Record found in cache!");
				return true;
			}	
		}

		return false; 
	}

	/**
	 * Fetch patient info from database
	 * @param pObj
	 * @param pId
	 */
	public void fetchPatientInfoFromDB(Patient pObj, String pId) {

		// Establish connection with Cassandara DB on client
		Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
		Session session = cluster.connect("hospitalDatabase");

		// Fetch data from both tables in DB
		// Patient info 
		Statement personalData= QueryBuilder.select().all().from("hospitalDatabase","personal_data").allowFiltering().where(QueryBuilder.eq("pid",pId));
		ResultSet personalRes = session.execute(personalData);
		for (Row row : personalRes) {

			pObj.setPid(pId);
			pObj.setFirst_name(row.getString("first_name"));
			pObj.setLast_name(row.getString("last_name"));
			pObj.setEmail(row.getString("email"));
			pObj.setAddress(row.getString("address"));
			pObj.setPhone(row.getString("phone"));
		}

		//Patient treatment info
		Statement treatmentData= QueryBuilder.select().all().from("hospitalDatabase","treatment_info").allowFiltering().where(QueryBuilder.eq("pid",pId));
		ResultSet treatmentRes = session.execute(treatmentData);
		for (Row row : treatmentRes) {

			pObj.setTreatment(row.getString("treatment"));
			pObj.setDiagnosis(row.getString("diagnosis"));
		}
		System.out.println("Record found in DB!");
	}

	/**
	 * Update Patient info
	 * @param pObj
	 */
	@PUT
	@Path("/AddPatientInfo")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addPatientInfo(Patient pObj) {

		if(isNullOrEmpty(pObj.getPid()) || isNullOrEmpty(pObj.getFirst_name()) 
				|| isNullOrEmpty(pObj.getLast_name()) || isNullOrEmpty(pObj.getAddress())
				|| isNullOrEmpty(pObj.getTreatment())) 
		{
			System.out.println("Error record... Enter all fields");
			return;
		}
		// Update cache if record present in cache
		if(pObj.getTreatment().equals(getPolicy()))
		{
			addPatientInfoToCache(pObj);
		}

		// Fetch patient info from cache
		addPatientInfoToDB(pObj);
	}

	/**
	 * Update patient info in cache
	 * @param pObj
	 * @return
	 */
	public void addPatientInfoToCache(Patient pObj) {

		// Add record to cache
		// Patient info
		IMap<String, Map<String, Object>> patientsMap = instance.getMap("patients");
		Map<String, Object> pInfo = new HashMap<>();
		pInfo.put("first_name", pObj.getFirst_name());
		pInfo.put("last_name", pObj.getLast_name());
		pInfo.put("email", pObj.getEmail());
		pInfo.put("address", pObj.getAddress());
		pInfo.put("phone", pObj.getPhone());
		patientsMap.put(pObj.getPid(), pInfo);

		// Patient treatment info
		IMap<String, Map<String, Object>> treatmentsMap = instance.getMap("treatment");
		Map<String, Object> tInfo = new HashMap<>();
		tInfo.put("treatment", pObj.getTreatment());
		tInfo.put("diagnosis", pObj.getDiagnosis());
		treatmentsMap.put(pObj.getPid(), tInfo);

		System.out.println("Patient info added to cache");
	}

	/**
	 * Update patient info in database
	 * @param pObj
	 */
	public void addPatientInfoToDB(Patient pObj) {

		// Establish connection with Cassandra DB on client
		Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
		Session session = cluster.connect("hospitalDatabase");

		// Add values to patient info table
		Statement personalData = QueryBuilder.insertInto("hospitalDatabase", "personal_data").value("pid", pObj.getPid())
				.value("first_name", pObj.getFirst_name()).value("last_name", pObj.getLast_name()).value("email", pObj.getEmail())
				.value("address", pObj.getAddress());
		session.execute(personalData);

		// Add values to patient treatment info table
		Statement treatmentData = QueryBuilder.insertInto("hospitalDatabase", "treatment_info").value("pid", pObj.getPid())
				.value("treatment", pObj.getTreatment()).value("diagnosis", pObj.getDiagnosis());
		session.execute(treatmentData);

		System.out.println("Patient info added to DB");
	}

	/**
	 * Remove patienet info from cache and db
	 * @param pId
	 */
	@DELETE
	@Path("/DeletePatientInfo")
	@Consumes(MediaType.TEXT_PLAIN)
	public void deletePatientInfo(String pId) {

		// Remove record from cache (No action if record not found)
		IMap<String, Map<String, Object>> patientsMap = instance.getMap("patients");
		patientsMap.evict(pId);
		IMap<String, Map<String, Object>> treatmentsMap = instance.getMap("treatment");
		treatmentsMap.evict(pId);
		System.out.println("Patient info removed from cache");
		
		// Remove record from database
		Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();;
		Session session = cluster.connect("hospitalDatabase");

		// Delete record from patient info table
		Statement personalData = QueryBuilder.delete().from("hospitalDatabase", "personal_data").where(QueryBuilder.eq("pid", pId));
		session.execute(personalData);		

		// Delete record from patient treatment info table
		Statement treatmentData = QueryBuilder.delete().from("hospitalDatabase", "treatment_info").where(QueryBuilder.eq("pid", pId));
		session.execute(treatmentData);
		System.out.println("Patient info removed from DB");
		
		// Send Email
		addToMessageQueue(email_subject, "Record deleted for Patient - " + pId);
		consumeMessage(email_subject);

		// Performa analytics
		addToMessageQueue(analytics_subject, "Record deleted for Patient - " + pId);
		consumeMessage(analytics_subject);
	}
	
	/**
	 * Update Patient info
	 * @param pObj
	 */
	@POST
	@Path("/EditPatientInfo")
	@Consumes(MediaType.APPLICATION_JSON)
	public void editPatientInfo(Patient pObj) {

		if(isNullOrEmpty(pObj.getPid())){
			System.out.println("Invalid data... enter valid pid");
		}
		
		editPatientInfoInCache(pObj);
		
		editPatientInfoInDB(pObj);
		
		// Send Email
		addToMessageQueue(email_subject, "Record edited for Patient - " + pObj.getPid());
		consumeMessage(email_subject);

		// Perform analytics
		addToMessageQueue(analytics_subject, "Record edited for Patient - " + pObj.getPid());
		consumeMessage(analytics_subject);
	}
	
	public void editPatientInfoInCache(Patient pObj) {
		
		IMap<String, Map<String, Object>> patientsMap = instance.getMap("patients");
		Map<String, Object> pInfoMap = patientsMap.get(pObj.getPid());
		if(null == pInfoMap || pInfoMap.isEmpty())
		{
			return;
		}
		
		if(!isNullOrEmpty(pObj.getFirst_name()))
		{
			pInfoMap.put("first_name", pObj.getFirst_name());
		}
		
		if(!isNullOrEmpty(pObj.getLast_name()))
		{
			pInfoMap.put("last_name", pObj.getLast_name());
		}
		
		if(!isNullOrEmpty(pObj.getAddress()))
		{
			pInfoMap.put("address", pObj.getAddress());
		}
		
		if(!isNullOrEmpty(pObj.getEmail()))
		{
			pInfoMap.put("email", pObj.getEmail());
		}
		
		if(!isNullOrEmpty(pObj.getPhone()))
		{
			pInfoMap.put("phone", pObj.getPhone());
		}
		
		patientsMap.put(pObj.getPid(), pInfoMap);
		
		
		IMap<String, Map<String, Object>> treatmentsMap = instance.getMap("treatment");
		Map<String, Object> pTreatInfoMap = treatmentsMap.get(pObj.getPid());

		String oldPolicy = "";
		boolean evictFlag = false;
		if(!isNullOrEmpty(pObj.getTreatment()))
		{
			oldPolicy = (String)pInfoMap.get("treatment");
			pInfoMap.put("treatment", pObj.getTreatment());
			if(!pObj.getTreatment().equals(oldPolicy))
			{
				evictFlag = true;
			}
		}
		
		if(!isNullOrEmpty(pObj.getDiagnosis()))
		{
			pInfoMap.put("diagnosis", pObj.getDiagnosis());
		}		
		
		treatmentsMap.put(pObj.getPid(), pTreatInfoMap);
		
		if(evictFlag)
		{
			System.out.println("Evicted!");
			patientsMap.evict(pObj.getPid());
			treatmentsMap.evict(pObj.getPid());
		}

		System.out.println("Patient info edited in cache");
	}
	
	/**
	 * Edit data in DB
	 * @param pObj
	 */
	public void editPatientInfoInDB(Patient pObj) {

		// Establish connection with Cassandra DB on client
		Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
		Session session = cluster.connect("hospitalDatabase");

		boolean personalDataUpdate = false;
		StringBuilder personalDataQuery = new StringBuilder("Update personal_data Set ");
		
		// Build query for patient info
		if(!isNullOrEmpty(pObj.getFirst_name()))
		{
			personalDataQuery.append("first_name = '");
			personalDataQuery.append(pObj.getFirst_name());
			personalDataQuery.append("', ");
			personalDataUpdate = true;
		}
		
		if(!isNullOrEmpty(pObj.getLast_name()))
		{
			personalDataQuery.append("last_name = '");
			personalDataQuery.append(pObj.getLast_name());
			personalDataQuery.append("', ");
			personalDataUpdate = true;
		}
		
		if(!isNullOrEmpty(pObj.getAddress()))
		{
			personalDataQuery.append("address = '");
			personalDataQuery.append(pObj.getAddress());
			personalDataQuery.append("', ");
			personalDataUpdate = true;
		}
		
		if(!isNullOrEmpty(pObj.getEmail()))
		{
			personalDataQuery.append("first_name = '");
			personalDataQuery.append(pObj.getFirst_name());
			personalDataQuery.append("', ");
			personalDataUpdate = true;
		}
		
		if(!isNullOrEmpty(pObj.getPhone()))
		{
			personalDataQuery.append("phone = '");
			personalDataQuery.append(pObj.getPhone());
			personalDataQuery.append("', ");
			personalDataUpdate = true;
		}

		personalDataQuery.append("where pid = '");
		personalDataQuery.append(pObj.getPid());
		personalDataQuery.append("'");

		String personalData = personalDataQuery.toString().replace(", where", " where");
		if(personalDataUpdate) {
			session.execute(personalData);			
			System.out.println("Patient personal info edited in DB");
		}
		
		StringBuilder treatmentDataQuery = new StringBuilder("Update treatment_info Set ");
		boolean treatmentDataUpdate = false;
		// Build query for treatment info
		if(!isNullOrEmpty(pObj.getTreatment()))
		{
			treatmentDataQuery.append("treatment = '");
			treatmentDataQuery.append(pObj.getTreatment());
			treatmentDataQuery.append("', ");
			treatmentDataUpdate = true;
		}
		
		if(!isNullOrEmpty(pObj.getDiagnosis()))
		{
			treatmentDataQuery.append("diagnosis = '");
			treatmentDataQuery.append(pObj.getDiagnosis());
			treatmentDataQuery.append("', ");
			treatmentDataUpdate = true;
		}		

		treatmentDataQuery.append("where pid = '");
		treatmentDataQuery.append(pObj.getPid());
		treatmentDataQuery.append("'");

		String treatmentData = treatmentDataQuery.toString().replace(", where", " where");			
		if(treatmentDataUpdate){
			session.execute(treatmentData);			
			System.out.println("Patient treatment info edited in DB");
		}
	}

	/**
	 * Fetch policy from policy server
	 * @return Current policy
	 */
	public String getPolicy() {

		String strCurrentPolicy = "";
		URL url;
		HttpURLConnection con;

		String strLine;
		StringBuffer strJSONObj = new StringBuffer();
		BufferedReader br;

		try {	
			// Generate http request to get policy
			url = new URL("http://localhost:9080/PolicyServer/rest/policy/GetPolicy");
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			// Read response on request
			br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while (null != (strLine = br.readLine())) {
				strJSONObj.append(strLine);
			}
			br.close();

			// fetch policy from executed request
			JSONParser parser = new JSONParser();
			Object jsonReceived = parser.parse(strJSONObj.toString());
			strCurrentPolicy = (String) ((JSONObject)jsonReceived).get("currPolicy");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return strCurrentPolicy;
	}

	/**
	 * Adds message to Message broker queue
	 * @param subject
	 * @param strMsg
	 */
	public void addToMessageQueue(String subject, String strMsg)
	{
		try {

			// Getting JMS connection from the server and starting it
			javax.jms.ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			javax.jms.Connection connection = connectionFactory.createConnection();
			connection.start();
			// JMS messages are sent and received using a Session. We will
			// create here a non-transactional session object. If you want
			// to use transactions you should set the first parameter to 'true'
			javax.jms.Session session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
			// Destination represents here our queue 'VALLYSOFTQ' on the
			// JMS server. You don't have to do anything special on the
			// server to create it, it will be created automatically.
			javax.jms.Destination destination = session.createQueue(subject);
			// MessageProducer is used for sending messages (as opposed
			// to MessageConsumer which is used for receiving them)
			javax.jms.MessageProducer producer = session.createProducer(destination);
			// We will send a small text message saying 'Hello' in Japanese
			TextMessage message = session.createTextMessage(strMsg);
			// Here we are sending the message!
			producer.send(message);
			System.out.println("Sent '" + message.getText() + "'");

			connection.close();
		}
		catch(JMSException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Consumes message from message broker queue	
	 * @param subject
	 */
	public void consumeMessage(String subject)
	{
		try {

			// Getting JMS connection from the server
			javax.jms.ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			javax.jms.Connection connection = connectionFactory.createConnection();
			connection.start();

			// Creating session for seding messages
			javax.jms.Session session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);

			// Getting the queue
			javax.jms.Destination destination = session.createQueue(subject);

			// MessageConsumer is used for receiving (consuming) messages
			javax.jms.MessageConsumer consumer = session.createConsumer(destination);

			// Here we receive the message.
			// By default this call is blocking, which means it will wait
			// for a message to arrive on the queue.
			javax.jms.Message message = consumer.receive();

			// There are many types of Message and TextMessage
			// is just one of them. Producer sent us a TextMessage
			// so we must cast to it to get access to its .getText()
			// method.
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				System.out.println("Received '" + textMessage.getText()    + "'");
			}
			connection.close();
		}
		catch(JMSException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if passed string is null or empty
	 * @param str
	 * @return
	 */
	static boolean isNullOrEmpty(String str) {

		if(null == str || str.isEmpty()) {
			return true;
		}
		return false;
	}
}