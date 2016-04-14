package pt.upa.broker.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jws.WebService;
import javax.xml.registry.JAXRException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

@WebService(
	    endpointInterface="pt.upa.broker.ws.BrokerPortType",
	    wsdlLocation="broker.1_0.wsdl",
	    name="UpaBroker",
	    portName="BrokerPort",
	    targetNamespace="http://ws.broker.upa.pt/",
	    serviceName="BrokerService"
	)
public class BrokerPort implements BrokerPortType{
	
	private List<TransporterClient> clientHandlers = new ArrayList<TransporterClient>();
	private List<BrokerJob> jobs = new ArrayList<BrokerJob>();
	private String[] centerTravels = {"Lisboa","Leiria","Santarém","Castelo Branco","Coimbra",
			"Aveiro","Viseu","Guarda"};
	private String[] southTravels = {"Setúbal","Évora","Portalegre","Beja","Faro"};
	private String[] northTravels = {"Porto","Braga","Viana do Castelo","Vila Real","Bragança"};
	
	public BrokerPort(){
	}
	
	public void initHandlersSearch(){
		
		System.out.printf("Contacting UDDI to find Transporters...");
		UDDINaming uddiNaming = null;
		
		try {
			uddiNaming = new UDDINaming("http://localhost:9090");
		
			Collection<String> endpointAddresses = uddiNaming.list("UpaTransporter%");
			ArrayList<String> urls = (ArrayList<String>) endpointAddresses;
			
			for (String url: urls){
				
				System.out.println(url);
				TransporterClient clientHandler = new TransporterClient("http://localhost:9090");
				
				clientHandler.setEndpointAddress(url);
				clientHandler.initServiceSearch();
				
			    addClientHandler(clientHandler);  
			    // Here, every transporterClient stays with its own transporterServer
			}
			
		} catch (JAXRException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String ping(String name) {
		
		String pong = "";
		
		for(TransporterClient tc: clientHandlers){
			pong += tc.ping(name);
		}
		
		return pong;
	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception,
			UnknownLocationFault_Exception {
		
		int index = jobs.size();
		String id = "" + index, info = null;
		
		verifyErrorCases(origin, destination, price);
		
		BrokerJob job = createJobRequested(id, origin, destination, price, JobState.REQUESTED);
		
		JobView view = bestOffer(origin, destination, price, id);

		job = changeJob(view, id);

		try {
			info = decideOffer(job, price);
		} catch (UnknownTransportFault_Exception e) {/*No special treatment, because this will never happen*/}
	
		return info;
	}

	@Override
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		
		BrokerJob job = getJobById(id);
		String idT = job.getTransporterIdentifier();
		TransporterClient clientHandler = getTransporterByJobId(id);		
		
		if(clientHandler == null){
			UnknownTransportFault fault = new UnknownTransportFault();
			fault.setId(id);
			throw new UnknownTransportFault_Exception("The specified transport doesn't exist",fault);
		} //meh
		
		JobView view = clientHandler.jobStatus(idT);
		
		if(view == null){
			UnknownTransportFault fault = new UnknownTransportFault();
			fault.setId(id);
			throw new UnknownTransportFault_Exception("The specified transport doesn't exist",fault);
		}

		changeJob(view, id);
		view.setJobIdentifier(id); 
		
		return convertJobViewToTransportView(view);
	}

	@Override
	public List<TransportView> listTransports() {

		for(TransporterClient tc: clientHandlers){			
			changeJobs(tc.listJobs());
		} // Update broker jobs
		
		return convertJobsList();
	}

	public String getIdByTransporterIdentifier(String idT, String companyName){
				
		for(BrokerJob job: jobs){
						
			if(job.getTransporterIdentifier().equals(idT) && job.getCompanyName().equals(companyName)){
				return job.getIdentifier();
			}
		}
		
		return null; // This will never happen
	}

	@Override
	public void clearTransports() {
		
		for(TransporterClient tc: clientHandlers){
			tc.clearJobs();
		}
		jobs.clear();
	}
	
	public void addClientHandler(TransporterClient client){
		clientHandlers.add(client);
	}
	
	public void addJob(BrokerJob job){
		jobs.add(job);
	}
	
	public BrokerJob getJob(int id) throws UnknownTransportFault_Exception{
		
		BrokerJob job = null;
		
		try{
			job = jobs.get(id);
		} catch(IndexOutOfBoundsException e){
			UnknownTransportFault fault = new UnknownTransportFault();
			fault.setId(""+id);
			throw new UnknownTransportFault_Exception("The specified job doesn't exist.", fault);
		}
		
		return job;
	}
	
	public BrokerJob getJobById(String id) throws UnknownTransportFault_Exception{
		
		int index = Integer.parseInt(id);
		
		return getJob(index);
	}
	
	public TransporterClient getTransporterByJobId(String id) throws UnknownTransportFault_Exception {
		
		BrokerJob job = getJobById(id);
		
		String serviceName =  job.getCompanyName();
		
		for(TransporterClient tc: clientHandlers){
			if(tc.getServiceName() != null){
				if(tc.getServiceName().equals(serviceName)){
					return tc;
				}
			}
		}
		
		return null;
	}
	
	public BrokerJob createJob(String companyName, String id, String idT, String origin, String destination, int price, JobState state){

		BrokerJob job = new BrokerJob(companyName, id, idT, origin, destination, price, state);
		
		addJob(job);
		
		return job;
	}
	
	public BrokerJob createJobRequested(String id, String origin, String destination, int price, JobState state){

		BrokerJob job = new BrokerJob(id, origin, destination, price, state);
		
		addJob(job);
		
		return job;
	}
	
	public void verifyErrorCases(String origin, String destination, int price) throws UnknownLocationFault_Exception, InvalidPriceFault_Exception{
		
		if(price < 0){
			InvalidPriceFault fault = new InvalidPriceFault();
			fault.setPrice(price);
			throw new InvalidPriceFault_Exception("The price must be positive.", fault);
		}
		
		if(!(containsLocation(centerTravels,origin)) && !(containsLocation(southTravels, origin)) 
				&& !(containsLocation(northTravels, origin))){
			UnknownLocationFault fault = new UnknownLocationFault();
			fault.setLocation(origin);
			throw new UnknownLocationFault_Exception("Unknown origin.", fault);
		}
				
		if(!(containsLocation(centerTravels,destination)) && !(containsLocation(southTravels, destination)) 
				&& !(containsLocation(northTravels, origin))){
			UnknownLocationFault fault = new UnknownLocationFault();
			fault.setLocation(destination);
			throw new UnknownLocationFault_Exception("Unknown destination.", fault);
		}
	}
	
	public boolean containsLocation(String[] vector, String name){
		
		for(String s: vector){
			if(s.equals(name)) return true;
		}
		
		return false;
	}

	public String convertJobStateView(JobStateView state){
		
		String name = state.name(), nameConverted;
		
		switch(name){
			case "PROPOSED":
				nameConverted = "BUDGETED";
				break;
			case "REJECTED":
				nameConverted = "FAILED";
				break;
			case "ACCEPTED":
				nameConverted = "BOOKED";
				break;
			case "HEADING":
				nameConverted = name;
				break;
			case "ONGOING":
				nameConverted = name;
				break;
			case "COMPLETED":
				nameConverted = name;
				break;
			default:
				nameConverted = ""; // This will never happen, but could throw an exception
				break;
		}

		return nameConverted;
	}
	
	public BrokerJob convertJobViewToBrokerJob(JobView view){

		BrokerJob newBj = new BrokerJob();
	    JobStateView stateVw = view.getJobState();
	    String state = convertJobStateView(stateVw);
	    
	    newBj.setCompanyName(view.getCompanyName());
	    newBj.setTransporterIdentifier(view.getJobIdentifier());
	    newBj.setDestination(view.getJobDestination());
	    newBj.setOrigin(view.getJobOrigin());
	    newBj.setPrice(view.getJobPrice());
	    newBj.setState(JobState.fromValue(state));
	    
	    return newBj;
	}
	
	public TransportView convertJobViewToTransportView(JobView view){
		
		TransportView newTv = new TransportView();
	    JobStateView stateVw = view.getJobState();
	    String state = convertJobStateView(stateVw);
	    
	    newTv.setId(view.getJobIdentifier());
	    newTv.setTransporterCompany(view.getCompanyName());
	    newTv.setDestination(view.getJobDestination());
	    newTv.setOrigin(view.getJobOrigin());
	    newTv.setPrice(view.getJobPrice());
	    newTv.setState(TransportStateView.fromValue(state));
	    
	    return newTv;
	}
	
	public TransportView convertBrokerJobToTransportView(BrokerJob job){
		
		TransportView newTv = new TransportView();
	    String state = job.getState().name();
	    
	    newTv.setId(job.getIdentifier());
	    newTv.setTransporterCompany(job.getCompanyName());
	    newTv.setDestination(job.getDestination());
	    newTv.setOrigin(job.getOrigin());
	    newTv.setPrice(job.getPrice());
	    newTv.setState(TransportStateView.fromValue(state));
	    
	    return newTv;
	}
	
	public JobView bestOffer(String origin, String destination, int price, String id) throws UnavailableTransportFault_Exception, 
			UnknownLocationFault_Exception, InvalidPriceFault_Exception{
		
		int minPrice = Integer.MAX_VALUE, givenPrice;
		JobView temp = null, view = null;
		List<JobView> proposals = new ArrayList<JobView>();
		
		for(TransporterClient tc: clientHandlers){
			try {
				temp = tc.requestJob(origin,destination,price);
				proposals.add(temp);
				
				if(temp == null){
					continue;
				}
			} catch (BadLocationFault_Exception e) { //Sítio desconhecido 
				UnknownLocationFault fault = new UnknownLocationFault();
				fault.setLocation(e.getFaultInfo().getLocation());
				throw new UnknownLocationFault_Exception(e.getMessage(),fault);
				
			} catch (BadPriceFault_Exception e) {
				InvalidPriceFault fault = new InvalidPriceFault();
				fault.setPrice(e.getFaultInfo().getPrice());
				throw new InvalidPriceFault_Exception(e.getMessage(),fault);
			}
			
			tc.setServiceName(temp.getCompanyName());
			givenPrice = temp.getJobPrice();
			
			if(givenPrice <= minPrice){
				minPrice = givenPrice;
				view = temp;
			}
		}
		
		if(view == null){
			UnavailableTransportFault fault = new UnavailableTransportFault();
			fault.setOrigin(origin);
			fault.setDestination(destination);
			throw new UnavailableTransportFault_Exception("No transporters available.", fault);
		}
		/*
		TransporterClient tc = null;
		
		for(JobView prop: proposals){
			if(prop != view){
				try {
					tc = getTransporterByJobId(this.getIdByTransporterIdentifier(prop.getJobIdentifier(),prop.getCompanyName()));
				} catch (UnknownTransportFault_Exception e) {/* Do nothing}
				try {
					tc.decideJob(prop.getJobIdentifier(), false);
				} catch (BadJobFault_Exception e) {/*Do nothing}				
			}
		}*/
				
		return view;
	} // This function needs to be shortened, too confusing
	
	public String decideOffer(BrokerJob job, int price) throws UnknownTransportFault_Exception, UnavailableTransportPriceFault_Exception,
		UnavailableTransportFault_Exception{
		
		String id = job.getIdentifier(), idT = job.getTransporterIdentifier();
		int bestPrice = job.getPrice();
		TransporterClient clientHandler = getTransporterByJobId(id);
		JobView view = null;
		
		if(clientHandler == null){
			UnavailableTransportFault fault = new UnavailableTransportFault();
			fault.setOrigin(job.getOrigin());
			fault.setDestination(job.getDestination());
			throw new UnavailableTransportFault_Exception("No transporters available.", fault);
		}
		
		if(bestPrice >= price){
			try {
				view = clientHandler.decideJob(idT, false);
			} catch (BadJobFault_Exception e) {
				UnknownTransportFault fault = new UnknownTransportFault();
				fault.setId(id);
				throw new UnknownTransportFault_Exception(e.getMessage(),fault);
			} // This is not expected to happen 
			
			changeJob(view,id);
			
			UnavailableTransportPriceFault fault = new UnavailableTransportPriceFault();
			fault.setBestPriceFound(bestPrice);
			throw new UnavailableTransportPriceFault_Exception("No transporters with the price requested.",fault);
		} else{
			
			try {
				view = clientHandler.decideJob(idT, true);
			} catch (BadJobFault_Exception e) {
				UnknownTransportFault fault = new UnknownTransportFault();

				fault.setId(id);
				
				throw new UnknownTransportFault_Exception(e.getMessage(),fault);
			}
		}
		
		changeJob(view,id);
		
		return id;
	}
	
	public BrokerJob changeJob(JobView view, String id){
		
		int index = Integer.parseInt(id);
				 
		BrokerJob job = convertJobViewToBrokerJob(view);
		
		job.setIdentifier(id);
		jobs.add(index,job);
		
		return job;
	}
	
	public void changeJobs(List<JobView> views){
							
		for(JobView view: views){
			String id = this.getIdByTransporterIdentifier(view.getJobIdentifier(), view.getCompanyName());
			changeJob(view, id);
		}
	}
	
	public List<TransportView> convertJobsList(){
		
		List<TransportView> convertedList = new ArrayList<TransportView>();
		
		for(BrokerJob job: jobs){
			convertedList.add(convertBrokerJobToTransportView(job));
		}
		
		return convertedList;
	}
	
}
