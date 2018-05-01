package policyPkg;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Policy Server function Implementaion
 *
 */
@Path("/policy")
public class PolicyImpl {

	/**
	 * Fetch policy from policy server
	 * @return
	 */
	@GET
	@Path("/GetPolicy")
	@Produces(MediaType.APPLICATION_JSON)
	public Policy getPolicy() {

		URL url;
		HttpURLConnection httpConnection;

		String strLine;
		String strCurrentPolicy = "";
		StringBuffer strJSONObj = new StringBuffer();

		BufferedReader br;
		Policy policyObj = new Policy(); 

		try {
			// Call mongoDB hosted on mLab and 
			// access appropriate document database to get policy valye
			url = new URL("https://api.mlab.com/api/1/databases/policyserver/collections/policy?apiKey=rB0qZLi8zYa4Otbjo_8Ne13d7HJKcMvF");
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestMethod("GET");

			br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));

			while (null != (strLine = br.readLine())) {
				strJSONObj.append(strLine);
			}
			br.close();

			JSONParser parser = new JSONParser();
			Object jsonReceived = parser.parse(strJSONObj.toString());
			JSONArray policyJSON = (JSONArray) jsonReceived;

			// Return policy value received in JSON format
			for(Object object: policyJSON) {
				JSONObject obj =(JSONObject)object;
				strCurrentPolicy = (String) obj.get("currPolicy");
			}

			if(null != strCurrentPolicy && !strCurrentPolicy.isEmpty()) {
				policyObj.setCurrPolicy(strCurrentPolicy);
			}
			
			System.out.println("Policy fetched - " + strCurrentPolicy);
		} 
		catch(Exception e) {
			e.printStackTrace();
		}

		return policyObj;
	}

	/**
	 * Update policy on policy server
	 * @param updatedPolicy
	 */
	@PUT
	@Path("/UpdatePolicy")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updatePolicy(Policy updatedPolicy) {

		// Connect to mongoDB on mLab to update the policy value
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPut httpPut = new HttpPut("https://api.mlab.com/api/1/databases/policyserver/collections/policy?apiKey=rB0qZLi8zYa4Otbjo_8Ne13d7HJKcMvF");
		JSONObject updatedPolicyJSON;
		try {

			updatedPolicyJSON = new JSONObject();
			updatedPolicyJSON.put("currPolicy", updatedPolicy.getCurrPolicy());

			StringEntity entity = new StringEntity(updatedPolicyJSON.toString());
			httpPut.setEntity(entity);
			httpPut.setHeader("Accept", "application/json");
			httpPut.setHeader("Content-type", "application/json");

			// Send new policy in json format via HTTP request
			CloseableHttpResponse res = client.execute(httpPut);
			res.close();
			client.close();
			System.out.println("Policy updated!");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}