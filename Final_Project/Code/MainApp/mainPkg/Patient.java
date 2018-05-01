package mainAppPkg;

/**
 * 
 * Patient class
 *
 */
public class Patient {

	// Patient id
	String pid;
	// Patient first name
	String first_name;
	// Patient first name
	String last_name;
	// Patient email id
	String email;
	// Patient address
	String address;
	// Patient phone number
	String phone;
	// Patient treatment (holds policy value)
	String treatment;
	// Patient diagnosis
	String diagnosis;

	public Patient() {
		this.pid = "";
		this.first_name = "";
		this.last_name = "";
		this.email = "";
		this.address = "";
		this.address = "";
		this.phone = "";
		this.treatment = "";
		this.diagnosis = "";
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getFirst_name() {
		return first_name;
	}
	
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}
	
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getTreatment() {
		return treatment;
	}

	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}
}
