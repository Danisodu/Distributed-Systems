package pt.upa.broker.ws;

import pt.upa.broker.ws.JobState;

public class BrokerJob {
	
 	private String companyName;
    private String identifier;
    private String transporterIdentifier;
    private String origin;
    private String destination;
    private int price;
    private JobState state;
    
    public BrokerJob(){}
    
    public BrokerJob(String jobCompanyName, String jobId, String transporterId, String jobOrigin,
    		String jobDestination, int jobPrice, JobState jobState) {
    	
    	setCompanyName(jobCompanyName);
    	setIdentifier(jobId);
    	setTransporterIdentifier(transporterId);
    	setOrigin(jobOrigin);
    	setDestination(jobDestination);
    	setPrice(jobPrice);
    	setState(jobState);
	}
    
    public BrokerJob(String jobId, String jobOrigin,
    		String jobDestination, int jobPrice, JobState jobState) {
    	
    	setCompanyName(null);
    	setIdentifier(jobId);
    	setTransporterIdentifier(null);
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

	public String getTransporterIdentifier() {
		return transporterIdentifier;
	}

	public void setTransporterIdentifier(String transporterId) {
		this.transporterIdentifier = transporterId;
	}
}
