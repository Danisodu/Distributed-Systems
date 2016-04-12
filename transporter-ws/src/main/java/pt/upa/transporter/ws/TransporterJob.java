package pt.upa.transporter.ws;

public class TransporterJob {
	
 	private String companyName;
    private String identifier;
    private String origin;
    private String destination;
    private int price;
    private JobState state;
    
    public TransporterJob(String jobCompanyName, String jobIdentifier, String jobOrigin,
    		String jobDestination, int jobPrice, JobState jobState) {
    	
    	setCompanyName(jobCompanyName);
    	setIdentifier(jobIdentifier);
    	setOrigin(jobOrigin);
    	setDestination(jobDestination);
    	setPrice(jobPrice);
    	setState(jobState);
	}

	public String getCompanyName() {
		return companyName;
	}
	
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getOrigin() {
		return origin;
	}
	
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	public String getDestination() {
		return destination;
	}
	
	public void setDestination(String destination) {
		this.destination = destination;
	}
	
	public int getPrice() {
		return price;
	}
	
	public void setPrice(int price) {
		this.price = price;
}

	public JobState getState() {
		return state;
	}

	public void setState(JobState state) {
		this.state = state;
	}
}

