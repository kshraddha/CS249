package policyPkg;

/**
 * 
 * Policy class
 *
 */
public class Policy 
{
	// Policy stored on / retrieved from server 
	String currPolicy;

	public Policy()
	{
		this.currPolicy = "";
	}
	
	public String getCurrPolicy() 
	{
		return currPolicy;
	}

	public void setCurrPolicy(String currPolicy) 
	{
		this.currPolicy = currPolicy;
	}
	
	@Override
	public String toString()
	{
		return "Policy = '" + this.currPolicy + "' ";
	}
}