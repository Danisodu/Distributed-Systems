package pt.upa.broker.ws;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.registry.JAXRException;

import javax.xml.ws.BindingProvider;

import javax.xml.ws.WebServiceContext;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;
import pt.upa.transporter.ws.cli.TransporterClientException;


@WebService(
	    endpointInterface="pt.upa.broker.ws.BrokerPortType",
	    wsdlLocation="broker.1_0.wsdl",
	    name="UpaBroker",
	    portName="BrokerPort",
	    targetNamespace="http://ws.broker.upa.pt/",
	    serviceName="BrokerService"
	)public class BrokerPort implements BrokerPortType {
	
	//###################################Handlers config stuff###############################
	
	//duvidas no que isto faz?????
	@Resource
	private WebServiceContext webServiceContext;

	private static String TOKEN = "BrokerServer";
	//#######################################################################################



	private List<TransporterClient> clientHandlers = new ArrayList<TransporterClient>();
	private TreeMap<String,TransportView> jobs = new TreeMap<String,TransportView>();
	private String[] centerTravels = {"Lisboa","Leiria","Santarém","Castelo Branco","Coimbra",
			"Aveiro","Viseu","Guarda"};
	private String[] southTravels = {"Setúbal","Évora","Portalegre","Beja","Faro"};
	private String[] northTravels = {"Porto","Braga","Viana do Castelo","Vila Real","Bragança"};
	private BrokerPortType broker2 = null;
	private int identifier;
//	private int i = 0;

	public BrokerPort(int i) {
		identifier = i;
	}
	
	public void initHandlersSearch(){
		
		System.out.printf("Contacting UDDI to find Transporters...\n\n");
		UDDINaming uddiNaming = null;
		String uddiURL = "http://localhost:9090";
		
		try {
			uddiNaming = new UDDINaming(uddiURL);
		
			Collection<UDDIRecord> records = uddiNaming.listRecords("UpaTransporter%");
			
			for (UDDIRecord record: records){
				
				TransporterClient clientHandler = null;
				
				try {
					clientHandler = new TransporterClient(record.getUrl());
					clientHandler.setServiceName(record.getOrgName());
				    addClientHandler(clientHandler);  
				} catch (TransporterClientException e) {
					e.printStackTrace();
				}
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
		
		/*if(i==0){
			try {
				System.out.println("Esperar 4s");
				Thread.sleep(4000);
				i++;
				System.out.println(i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("vou mandar");*/

		return pong;
	}
	
	@Override
	public void update(List<TransportView> view) {
		
		if(identifier == 1) {
			if(broker2 == null) initSecondaryBroker();
						
			if(broker2!=null){
				System.out.println("Broker1: Updating the other broker...\n");
				broker2.update(convertJobsList());
			}
		} else {
			updateJobsList(view);
			System.out.println("Broker2: Updated :)");
		}
	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception,
			UnknownLocationFault_Exception {
		
		String info = null;
		verifyErrorCases(origin, destination, price);
		JobView view = bestOffer(origin, destination, price);
		TransportView job = convertJobViewToTransportView(view);
		
		addJob(job);
		
		try {
			info = decideOffer(job, price);
		} catch (UnknownTransportFault_Exception e) {/*No special treatment, because this will not happen*/}
		
		if(identifier==1) update(new ArrayList<TransportView>());
		
		return info;
	}

	@Override
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		
		TransporterClient clientHandler = getTransporterByJobId(id);
		
		if(clientHandler == null) {
			UnknownTransportFault fault = new UnknownTransportFault();
			fault.setId(id);
			throw new UnknownTransportFault_Exception("The specified transport doesn't exist",fault);
		} //hmmm
		
		JobView view = clientHandler.jobStatus(id);
		
		if(view == null){
			UnknownTransportFault fault = new UnknownTransportFault();
			fault.setId(id);
			throw new UnknownTransportFault_Exception("The specified transport doesn't exist",fault);
		}
		
		if(identifier==1) update(new ArrayList<TransportView>());

		return changeJob(view);
	}

	@Override
	public List<TransportView> listTransports() {
		if(identifier==1) update(new ArrayList<TransportView>());
		return convertJobsList();
	}
	
	@Override
	public void clearTransports() {
		
		for(TransporterClient tc: clientHandlers){
			tc.clearJobs();
		}
		
		jobs.clear();
		
		if(identifier==1) update(new ArrayList<TransportView>());
	}
	
	public void setId(int i){
		identifier = i;
	}
	
	public void addClientHandler(TransporterClient client){
		clientHandlers.add(client);
	}
	
	public void addJob(TransportView job){
		jobs.put(job.getId(), job);
	}
	
	public TransportView getView(String id) throws UnknownTransportFault_Exception{
		
		TransportView job;
		
		if(id == null){
			UnknownTransportFault fault = new UnknownTransportFault();
			fault.setId(id);
			throw new UnknownTransportFault_Exception("The specified job doesn't exist.", fault);
		}
		
		if(jobs.containsKey(id)){
			job = jobs.get(id);
		} else{
			UnknownTransportFault fault = new UnknownTransportFault();
			fault.setId(id);
			throw new UnknownTransportFault_Exception("The specified job doesn't exist.", fault);
		}
				
		return job;
	}
	
	public TransporterClient getTransporterByJobId(String id) throws UnknownTransportFault_Exception {
		
		TransportView job = getView(id);
		String serviceName = job.getTransporterCompany();

		for(TransporterClient tc: clientHandlers){
			if(tc.getServiceName() != null){
				if(tc.getServiceName().equals(serviceName)){
					return tc;
				}
			}
		}
		
		return null;  // Whenever the transporter client is null we assume that no transporters are available
	}
	
	public TransportView setJob(String id, String origin, String destination, Integer price, String companyName, TransportStateView state){
		
		TransportView job = new TransportView();
		
		job.setDestination(destination);
		job.setId(id);
		job.setOrigin(origin);
		job.setPrice(price);
		job.setState(state);
		job.setTransporterCompany(companyName);
		
		return job;
	}
	
	public TransportView createJob(String id, String origin, String destination, Integer price, String companyName, TransportStateView state){
		TransportView job = setJob(id, origin, destination, price, companyName, state);
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
				&& !(containsLocation(northTravels, destination))){
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
	
	public TransportView convertJobViewToTransportView(JobView view){
		
	    JobStateView stateVw = view.getJobState();
	    String state = convertJobStateView(stateVw);
	 
	    return setJob(view.getJobIdentifier(),view.getJobOrigin(),view.getJobDestination(),view.getJobPrice(),
	    		view.getCompanyName(),TransportStateView.fromValue(state));
	}
	
	public List<TransportView> convertJobsList(){
		
		TransportView job;
		changeJobs();
		
		TreeMap<String, TransportView> map = new TreeMap<String, TransportView>();
		
		for(String idKey: jobs.keySet()){
			try {
				job = getView(idKey);
				map.put(job.getId(), job);
			} catch (UnknownTransportFault_Exception e) { /* Nothing relevant to do*/ }
		}
		
		return new ArrayList<TransportView>(map.values());
	}
	
	public void updateJobsList(List<TransportView> view){
				
		TreeMap<String, TransportView> tMap = new TreeMap<String, TransportView>();
		
		for(TransportView tv: view){
			tMap.put(tv.getId(), tv);
		}
		
		jobs = tMap;
	}
	
	public JobView bestOffer(String origin, String destination, int price) throws UnavailableTransportFault_Exception, 
			UnknownLocationFault_Exception, InvalidPriceFault_Exception {
		
		int minPrice = Integer.MAX_VALUE, givenPrice;
		JobView temp = null, view = null;
		List<JobView> proposals = new ArrayList<JobView>();
		
		for(TransporterClient tc: clientHandlers){
			try {
				temp = tc.requestJob(origin,destination,price);
				
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
						
			proposals.add(temp);
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
		
		try {
			cancelProposals(proposals, view);
		} catch (UnknownTransportFault_Exception e) { /*Nothing relevant to be done*/ }
		
		return view;
	}
	
	public String decideOffer(TransportView job, int price) throws UnknownTransportFault_Exception, UnavailableTransportPriceFault_Exception,
		UnavailableTransportFault_Exception{
		
		String id = job.getId();
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
				view = clientHandler.decideJob(id, false);
			} catch (BadJobFault_Exception e) {
				UnknownTransportFault fault = new UnknownTransportFault();
				fault.setId(id);
				throw new UnknownTransportFault_Exception(e.getMessage(),fault);
			} // This is not expected to happen 
			
			changeJob(view);
			
			UnavailableTransportPriceFault fault = new UnavailableTransportPriceFault();
			fault.setBestPriceFound(bestPrice);
			throw new UnavailableTransportPriceFault_Exception("No transporters with the price requested.",fault);
		} else{
			
			try {
				view = clientHandler.decideJob(id, true);
			} catch (BadJobFault_Exception e) {
				UnknownTransportFault fault = new UnknownTransportFault();
				fault.setId(id);
				throw new UnknownTransportFault_Exception(e.getMessage(),fault);
			}
		}
		
		changeJob(view);
		
		return id;
	}
	
	public TransportView changeJob(JobView view){			 
		TransportView job = convertJobViewToTransportView(view);
		addJob(job);
		return job;
	}
	
	public void changeJobs(){
		
		TransporterClient tc = null;
		JobView view = null;
		
		for(String idKey: jobs.keySet()){
			try {
				tc = getTransporterByJobId(idKey);
			} catch (UnknownTransportFault_Exception e) { /*Just ignore*/ }
						
			view = tc.jobStatus(idKey);

			changeJob(view);
		}
	}
	
	public TransporterClient getTransporterByCompanyName(String companyName){
		
		for(TransporterClient tc: clientHandlers){
			if(tc.getServiceName().equals(companyName)){
				return tc;
			}
		}
		
		return null;
	}
	
	public void cancelProposals(List<JobView> props, JobView v) throws UnknownTransportFault_Exception{
			
			TransporterClient clientHandler = null;
			
			for(JobView prop: props){
				
				if(prop != v){
	
					clientHandler = getTransporterByCompanyName(prop.getCompanyName());
					
					if(clientHandler == null){
						UnknownTransportFault fault = new UnknownTransportFault();
						fault.setId("bleh");
						throw new UnknownTransportFault_Exception("The specified transport doesn't exist",fault);
					} //Will not happen

					
					try {
						clientHandler.decideJob(prop.getJobIdentifier(), false);
					} catch (BadJobFault_Exception e) { /*Do nothing*/ }				
				}
			}
	}	

	public void initSecondaryBroker() {
		
		String url = null;
		
		try {
			UDDINaming uddiNaming = new UDDINaming("http://localhost:9090");
			url = uddiNaming.lookup("UpaBrokerBackUp");			
		} catch(Exception e){ e.printStackTrace(); }
		
		if(url != null){
			System.out.println("Setting secondary broker ...");
			BrokerService service = new BrokerService();
			BrokerPortType port = service.getBrokerPort();
			System.out.println("Setting endpoint address ...");
			BindingProvider bindingProvider = (BindingProvider) port;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, url);
			
			broker2 = port;
		}		
	}

}		
  //<-----------------------2ªentrega------------------------->

	/*public Certificate GetCertificate()throws Exception{

		return caclient.GetCertificate("UpaBroker");
	}

	public PublicKey getPublicKey(Certificate brokercertificate ) throws Exception{

	
		PublicKey publicKey = brokercertificate.getPublicKey();

		return publicKey;

	}

	public PrivateKey getPrivateKeyFromkeystore(char[] keyStorePassowrd, String keyAlias, char[] keypassowrd) throws Exception {
		KeyStore keystore = readKeystoreFile(keyStorePassowrd);
		PrivateKey pkey= (PrivateKey) keystore.getKey(keyAlias, keypassowrd);

		return pkey;

	}

	public KeyStore readKeystoreFile(char[] keyStorePassowrd)throws Exception{
		Class cls = Class.forName("TransporterPort");
		ClassLoader cLoader = cls.getClassLoader();

		String keystore = "UpaBroker" + ".jks";
		InputStream file = cLoader.getResourceAsStream(keystore);

		KeyStore keystore1 = KeyStore.getInstance(KeyStore.getDefaultType());
		keystore1.load(file, keyStorePassowrd);
		
		return keystore1;


	}*/
