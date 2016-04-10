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
	
	List<TransporterClient> clientHandlers;
	List<BrokerJob> jobs = new ArrayList<BrokerJob>();
	//é preciso ter aqui alguns valores do uddi url etc etc?
	
	public BrokerPort(){
		initHandlersSearch();
	}
	
	public void initHandlersSearch(){
				
		try {
			UDDINaming uddiNaming = new UDDINaming("http://localhost:9090");
			
			Collection<String> endpointAddresses = uddiNaming.list("UpaTransporter%");
			ArrayList<String> addresses = (ArrayList<String>) endpointAddresses;
			
			
			for (String id: addresses){
				System.out.println(id);
				TransporterClient clientHandler = new TransporterClient("http://localhost:9090", "UpaTransporter1");
			    addClientHandler(clientHandler);  
			    //here, every transporterClient stays with its own transporterServer
			}
		} catch (JAXRException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String ping(String name) {
		return name;
	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, BadLocationFault_Exception, BadPriceFault_Exception, UnknownTransportFault_Exception, BadJobFault_Exception {
		
		String id = ""+jobs.size(), info; // Change
		BrokerJob job = createJob("Não atribuido", id, origin, destination, price, JobState.REQUESTED); // Ask about state changes

		// What if job variable doesn't have any best offer?
		job = bestOffer(origin, destination, price);
		
		// Id is the broker's identifier, while job.identifier() is the transporter's identifier
		job.setIdentifier(id);
		
		
		info = decideOffer(job, price);
	
		return info;
	}

	@Override
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		
		BrokerJob job = getJobById(id);
		String convertedId = job.getIdentifier(); //that's how the convertion is made
		TransporterClient clientHandler = getTransporterByJobId(id);
		
		/*if(clientHandler == null){
			UnknownTransportFault fault = new UnknownTransportFault();
			fault.setId(id);
			throw new UnknownTransportFault_Exception("",);
		}*/
				
		JobView view = clientHandler.jobStatus(convertedId);
		
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
		
		return null; //this will never happen
	}
	
	public BrokerJob createJob(String companyName, String id, String origin, String destination, int price, JobState state){

		BrokerJob job = new BrokerJob(companyName, id, origin, destination, price, state);
		
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
				nameConverted = ""; // this will never happen, change (?)
				break;
		}

		return nameConverted;
	}
	
	public BrokerJob convertJobView1(JobView view){

		BrokerJob newBj = new BrokerJob();
	    JobStateView stateVw = view.getJobState();
	    String state = convertJobStateView(stateVw);
	    
	    newBj.setIdentifier(view.getJobIdentifier());
	    newBj.setCompanyName(view.getCompanyName());
	    newBj.setDestination(view.getJobDestination());
	    newBj.setIdentifier(view.getJobIdentifier());
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
	
	public BrokerJob bestOffer(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception, UnavailableTransportFault_Exception{
		
		int minPrice = Integer.MAX_VALUE, givenPrice;
		JobView temp, view = null;
		
		if(clientHandlers.size() == 0){
			UnavailableTransportFault fault = new UnavailableTransportFault();
			fault.setOrigin(origin);
			fault.setDestination(destination);
			throw new UnavailableTransportFault_Exception("No transporters available.", fault);
		}

		for(TransporterClient tc: clientHandlers){
			
			temp = tc.requestJob(origin,destination,price);
			givenPrice = temp.getJobPrice();
			
			//verify conditions
			if(givenPrice <= minPrice){
				minPrice = givenPrice;
				view = temp;
			}
		}
		
		return convertJobView1(view);
	}
	
	public String decideOffer(BrokerJob job, int price) throws BadJobFault_Exception, UnknownTransportFault_Exception, UnavailableTransportPriceFault_Exception{
		
		int bestPrice = job.getPrice();
		String id = job.getIdentifier();
		TransporterClient clientHandler = getTransporterByJobId(id);
		JobView view;
		
		if(bestPrice >= price){
			view = clientHandler.decideJob(job.getIdentifier(), false);
			job = convertJobView1(view);
			UnavailableTransportPriceFault fault = new UnavailableTransportPriceFault();
			fault.setBestPriceFound(bestPrice);
			throw new UnavailableTransportPriceFault_Exception("No transporters with the price requested.",fault);
		} else{
			view = clientHandler.decideJob(job.getIdentifier(), true);
			job = convertJobView1(view); //this changes vector's job?
			//falta rejeitar todas as outras ofertas
		}
	
		return "Transport " + id + "handled by " + job.getCompanyName();
	} //lança erro caso não exista um transporte disponível com o preço pretendido

}
