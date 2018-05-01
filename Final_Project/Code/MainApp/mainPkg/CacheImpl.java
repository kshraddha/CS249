package mainAppPkg;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
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
 * Cache function implementation
 *
 */
@Path("/caching")
public class CacheImpl {

	static HazelcastInstance instance = Hazelcast.newHazelcastInstance();

	/**
	 * Clears cache and repopulates cache with updated values
	 * @param currPolicy
	 */
	@PUT
	@Path("/ClearAndUpdateCache")
	@Consumes(MediaType.TEXT_PLAIN)
	public void clearAndUpdateCache(String currPolicy){

		// Clears cache
		IMap<String, Map<String, Object>> patientInfoMap  = instance.getMap("patients");
		patientInfoMap.evictAll();
		IMap<String, Map<String, Object>> patientTreatmentMap  = instance.getMap("treatment");
		patientTreatmentMap.evictAll();	
		System.out.println("Cache Cleared!");

		// Updates cache contents
		updateCache(currPolicy, patientInfoMap, patientTreatmentMap);
	}

	/**
	 * Connect to database and update values in cache
	 * @param currPolicy
	 * @param patientInfoMap
	 * @param patientTreatmentMap
	 */
	public void updateCache(String currPolicy, IMap<String, Map<String, Object>> patientInfoMap, IMap<String, Map<String, Object>> patientTreatmentMap)
	{
		// Establish connection with Cassandara DB on client
		Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
		Session session = cluster.connect("hospitaldatabase");

		// Fetch treatment info for patients with appropriate policy
		Statement treatmentData = QueryBuilder.select().all().from("hospitalDatabase","treatment_info").allowFiltering().where(QueryBuilder.eq("treatment",currPolicy));
		ResultSet tResSet = session.execute(treatmentData);

		Map<String, Map<String, Object>> treatmentsMap = new HashMap<>();
		List<String> pIdList = new ArrayList<>();
		for (Row row : tResSet ) {
			pIdList.add(row.getString("pid"));
			Map<String, Object> p = new HashMap<>();
			p.put("treatment", row.getString("treatment"));
			p.put("diagnosis", row.getString("diagnosis"));
			treatmentsMap.put(row.getString("pid"), p);
		}
		patientTreatmentMap.putAll(treatmentsMap);

		// Fetch patient info for all pid from above results
		PreparedStatement patientData = session.prepare("select pid, first_name, address,last_name,email,phone from personal_data WHERE pid IN ? Allow Filtering");
		ResultSet pResSet = session.execute(patientData.bind(pIdList));

		Map<String, Map<String, Object>> patientsMap = new HashMap<>();
		for (Row row : pResSet) {
			Map<String, Object> p = new HashMap<>();
			p.put("first_name", row.getString("first_name"));
			p.put("last_name", row.getString("last_name"));
			p.put("email", row.getString("email"));
			p.put("address", row.getString("address"));
			p.put("phone", row.getString("phone"));
			System.out.println("Data added for "+ row.getString("pid") + " - map - "  + p);
			patientsMap.put(row.getString("pid"), p);
		}
		patientInfoMap.putAll(patientsMap);

		System.out.println("Cache Updated!");
	}
}