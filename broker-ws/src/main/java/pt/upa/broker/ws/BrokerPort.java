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
	
	List<TransporterClient> clientHandlers = new ArrayList<TransporterClient>();
	List<BrokerJob> jobs = new ArrayList<BrokerJob>();
	
	public BrokerPort(){
	}
	
	public void initHandlersSearch(){
		
		System.out.printf("Contacting UDDI...");
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
			    //here, every transporterClient stays with its own transporterServer
			}
			
		} catch (JAXRException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String ping(String name) {
		
		return clientHandlers.get(0).ping(name);
	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception,
			UnknownLocationFault_Exception {
		
		int index = jobs.size();
		String id = "" + index, info = null;
		BrokerJob job = createJobRequested(id, origin, destination, price, JobState.REQUESTED);

		job = changeJob(bestOffer(origin, destination, price, id), id);

		try {
			info = decideOffer(job, price);
		} catch (UnknownTransportFault_Exception e) {/*No special treatment, because this will never happen*/}
	
		return info;
	}

	@Override
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		
		BrokerJob job = getJobById(id);
		String convertedId = job.getTransporterIdentifier(); //that's how the convertion is made
		TransporterClient clientHandler = getTransporterByJobId(id);
		
		//if(clientHandler == null)
							
		JobView view = clientHandler.jobStatus(convertedId);
		
		if(view == null){
			UnknownTransportFault fault = new UnknownTransportFault();
			fault.setId(id);
			throw new UnknownTransportFault_Exception("The specified transport doesn't exist",fault);
		}
		
		view.setJobIdentifier(id);
		
		return convertJobView2(view);
	}

	@Override
	public List<TransportView> listTransports() {
		
		/*
		for(TransporterClient tc: clientHandlers){
			tc.clearJobs();
		}*/ return null; //juntar as duas listas
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
			fault.setId(""+id); //change
			throw new UnknownTransportFault_Exception("The specified job doesn't exist.", fault);
		}
		
		return job;
	}
	
	public BrokerJob getJobById(String id) throws UnknownTransportFault_Exception{
		
		int index = Integer.parseInt(id);
		
		return getJob(index);
	}
	
	public TransporterClient getTransporterByJobId(String id) throws UnknownTransportFault_Exception{
		
		BrokerJob job = getJobById(id);
		
		String serviceName =  job.getCompanyName();
		
		for(TransporterClient tc: clientHandlers){
			
			if(tc.getServiceName().equals(serviceName)){
				return tc;
			}
		}
		
		return null; //throw exception
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
	
	public BrokerJob convertJobView1(JobView view){

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
	
	public TransportView convertJobView2(JobView view){
		
		TransportView newTv = new TransportView();
	    JobStateView stateVw = view.getJobState();
	    String state = convertJobStateView(stateVw);
	    
	    newTv.setId(view.getJobIdentifier());
	    newTv.setTransporterCompany(view.getCompanyName());
	    newTv.setDestination(view.getJobDestination());
	    newTv.setId(view.getJobIdentifier());
	    newTv.setOrigin(view.getJobOrigin());
	    newTv.setPrice(view.getJobPrice());
	    newTv.setState(TransportStateView.fromValue(state));
	    
	    return newTv;
	}
	
	public JobView bestOffer(String origin, String destination, int price, String id) throws UnavailableTransportFault_Exception, UnknownLocationFault_Exception, InvalidPriceFault_Exception{
		
		int minPrice = Integer.MAX_VALUE, givenPrice;
		JobView temp = null, view = null;
		
		if(clientHandlers.size() == 0){
			UnavailableTransportFault fault = new UnavailableTransportFault();
			fault.setOrigin(origin);
			fault.setDestination(destination);
			throw new UnavailableTransportFault_Exception("No transporters available.", fault);
		}

		for(TransporterClient tc: clientHandlers){
			
			try {
				temp = tc.requestJob(origin,destination,price);
			} catch (BadLocationFault_Exception e) {
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
			
			//verify conditions
			if(givenPrice <= minPrice){
				minPrice = givenPrice;
				view = temp;
			}
		} //falta rejeitar as outras ofertas
				
		return view;
	}
	
	public String decideOffer(BrokerJob job, int price) throws UnknownTransportFault_Exception, UnavailableTransportPriceFault_Exception{
		
		String id = job.getIdentifier(), idT = job.getTransporterIdentifier();
		int bestPrice = job.getPrice();
		TransporterClient clientHandler = getTransporterByJobId(id);
		JobView view = null;
		
		if(clientHandler == null){
			System.out.println("ClientHandler in decideOffer is null");
		}
		
		if(bestPrice >= price){
			try {
				view = clientHandler.decideJob(idT, false);
			} catch (BadJobFault_Exception e) { // O job com esse id nao existe
				UnknownTransportFault fault = new UnknownTransportFault();
				
				fault.setId(id);
				
				throw new UnknownTransportFault_Exception(e.getMessage(),fault);
			}  //lança erro caso não exista um transporte disponível com o preço pretendido
			//isto pode chegar a não ser feito
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
				 
		BrokerJob job = convertJobView1(view);
		
		job.setIdentifier(id);
		jobs.add(index,job);
		
		return job;
	}
}
