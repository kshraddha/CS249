package mainAppPkg;


import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;

/**
 * Update PolicyServer policy and re-populate cache
 *
 */
@Path("/UpdatePolicy")
public class UpdatePolicy {

	/**
	 * 
	 * @param newPolicy
	 */
	@POST
	@Path("/SetNewPolicy")
	@Consumes(MediaType.TEXT_PLAIN)
	public void setNewPolicy(String newPolicy) 
	{
		CloseableHttpClient client;
		HttpPut httpPut;
		CloseableHttpResponse response;
		try 
		{
			// Call policy server update
			client = HttpClients.createDefault();
			httpPut = new HttpPut("http://localhost:9080/PolicyServer/rest/policy/UpdatePolicy");
			
			// Send new policy in JSON in the header
			JSONObject updatedData = new JSONObject();
			updatedData.put("currPolicy", newPolicy);

			StringEntity entity = new StringEntity(updatedData.toString());
			httpPut.setEntity(entity);
			httpPut.setHeader("Accept", "application/json");
			httpPut.setHeader("Content-type", "application/json");

			response = client.execute(httpPut);
		
			if(!response.getStatusLine().toString().equals("HTTP/1.1 404") && !response.getStatusLine().toString().equals("HTTP/1.1 500")) {
				// Update cache as policy server is updated
				callUpdateCache(newPolicy);
			}
			response.close();
			client.close();
			System.out.println("New policy set - " + newPolicy);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update cache on updating policy
	 * @param newPolicy
	 * @throws Exception
	 */
	public void callUpdateCache(String newPolicy) throws Exception {

		// Update cache once new policy is set
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPut request = new HttpPut("http://localhost:8080/MainApp/rest/caching/ClearAndUpdateCache");

		StringEntity entity = new StringEntity(newPolicy);
		request.setEntity(entity);
		request.setHeader("Accept", "text/plain");
		request.setHeader("Content-type", "text/plain");
		
		// Call caching to update cache
		CloseableHttpResponse response = client.execute(request);
		response.close();
		client.close();
		System.out.println("Calling update cache!");
	}	
}