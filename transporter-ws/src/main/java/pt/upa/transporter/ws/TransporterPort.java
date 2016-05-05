package pt.upa.transporter.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import javax.jws.WebService;

import javax.jws.HandlerChain;


@WebService(
	    endpointInterface="pt.upa.transporter.ws.TransporterPortType",
	    wsdlLocation="transporter.1_0.wsdl",
	    name="UpaTransporter",
	    portName="TransporterPort", 
	    targetNamespace="http://ws.transporter.upa.pt/",
	    serviceName="TransporterService"
	)

@HandlerChain(file="/handler-chain.xml")

public class TransporterPort implements TransporterPortType{
	
	private int id;
	private String companyName;
	private TreeMap<String, JobView> jobs = new TreeMap<String, JobView>();
	private String[] centerTravels = {"Lisboa","Leiria","Santarém","Castelo Branco","Coimbra",
				"Aveiro","Viseu","Guarda"};
	private String[] southTravels = {"Setúbal","Évora","Portalegre","Beja","Faro"};
	private String[] northTravels = {"Porto","Braga","Viana do Castelo","Vila Real","Bragança"};
	
	public TransporterPort(String name){
		companyName = name;
		id = createIdByCompanyName();
	}

	@Override
	public String ping(String name) {
		return companyName + " says " + name + ".\n";
	}

	@Override
	public JobView requestJob(String origin, String destination, int price)
			throws BadLocationFault_Exception, BadPriceFault_Exception {
		
		String[] travels;
		
		if(id%2==0){
			travels = northTravels;
		} else{
			travels = southTravels;
		}
		
		verifyErrorCases(origin, destination, price);
		
		if(verifyNullCases(origin, destination, travels, price)){ return null; }
		
		price = decidePrice(price);
		
		JobView newJv = createJob(companyName, Integer.toString(jobs.size()), origin, destination, price, JobStateView.PROPOSED); 
				
		return newJv;
	}

	@Override
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		
		JobView job = getJob(id);
		
		if(!job.getJobState().name().equals("PROPOSED")){
			BadJobFault fault = new BadJobFault();
			fault.setId(id);
			throw new BadJobFault_Exception("O id do job específicado não existe",fault); 
		}
		
		if(accept){ 
			Timer timer = new Timer();
			long time = generateRandom(5) + 1;
			
			job.setJobState(JobStateView.ACCEPTED);
			timer.schedule( new TimerTask(){
				@Override
				public void run() {
					String state = job.getJobState().name();

					if(state.equals("ACCEPTED")){
						job.setJobState(JobStateView.HEADING);
					}
					
					else if(state.equals("HEADING")){
						job.setJobState(JobStateView.ONGOING);
					}
					
					else{
						job.setJobState(JobStateView.COMPLETED);
						timer.cancel();
					}
					
					jobs.put(job.getJobIdentifier(), job);
				}
			}, time*1000, 30*1000);
		} else{
			job.setJobState(JobStateView.REJECTED);
		}
		
		return job;
	}

	@Override
	public JobView jobStatus(String id){
		
		JobView job = null;
		
		try{
			job = getJob(id);
		} catch(BadJobFault_Exception e){ return null; }
				
		return job;
	}

	@Override
	public List<JobView> listJobs() {
		return new ArrayList<JobView>(jobs.values());
	}

	@Override
	public void clearJobs() {
		jobs.clear();
	}

	public void setTransporterIdentifier(int identifier) {
		id = identifier;
	}

	public JobView getJob(String id) throws BadJobFault_Exception {
				
		JobView job;
		
		if(id == null){
			BadJobFault fault = new BadJobFault();
			fault.setId(id);
			throw new BadJobFault_Exception("O id do job específicado não existe",fault); 
		}
		
		if(jobs.containsKey(id)){
			job = jobs.get(id);
		} else{
			BadJobFault fault = new BadJobFault();
			fault.setId(id);
			throw new BadJobFault_Exception("O id do job específicado não existe",fault); 
		}
				
		return job;
	}		

	public void addJob(JobView job) {
		jobs.put(job.getJobIdentifier(), job);
	}

	public int createIdByCompanyName(){
		
		String id = companyName.replaceAll("\\D+","");
		
		int idConverted = Integer.parseInt(id);		
		
		return idConverted;
	}
	
	public JobView createJob(String companyName, String identifier, String origin, String destination, 
			int price, JobStateView state){
		
		JobView job = new JobView();
		
		job.setCompanyName(companyName);
		job.setJobIdentifier(identifier);
		job.setJobOrigin(origin);
		job.setJobDestination(destination);
		job.setJobPrice(price);
		job.setJobState(state);
		
		addJob(job);
		
		return job;
	}
	
	public void verifyErrorCases(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception{
		
		if(!(containsLocation(southTravels,origin)) && !(containsLocation(centerTravels, origin)) 
				&& !(containsLocation(northTravels, origin))){
			BadLocationFault fault = new BadLocationFault();
			fault.setLocation(origin);
			throw new BadLocationFault_Exception("Origem errada", fault);
		}
		
		else if(!(containsLocation(southTravels,destination)) && !(containsLocation(centerTravels, destination)) 
				&& !(containsLocation(northTravels, destination))){
			BadLocationFault fault = new BadLocationFault();
			fault.setLocation(destination);
			throw new BadLocationFault_Exception("Destino errado", fault);
		}
		
		if(price <0){
			BadPriceFault fault = new BadPriceFault();
			fault.setPrice(price);
			throw new BadPriceFault_Exception("O preço tem que ser positivo",fault);
		}
	}
	
	public boolean verifyNullCases(String origin, String destination, String[] travels, int price){
		
		boolean test = false;
		
		if(price > 100){ return true; }
		
		if(!(containsLocation(travels,origin)) && !(containsLocation(centerTravels, origin))){
			return true;
		}
		
		if(!(containsLocation(travels,destination)) && !(containsLocation(centerTravels, destination))){
			return true;
		}

		return test;
	}
	
	public boolean containsLocation(String[] vector, String name){
		
		for(String s: vector){
			if(s.equals(name)) return true;
		}
		
		return false;
	}
	
	public int generateRandom(int max){
		
		Random rand = new Random();
		
		return rand.nextInt(max);
	}
	
	public int decidePrice(int price){
		
		int priceRes = 0;
		
		if(price <= 10){
			priceRes = generateRandom(price-1) + 1;
		}
		
		else if(10 < price && price <= 100){
			if(price%2 != 0){
				if(id%2 != 0){
					priceRes = generateRandom(price-1) + 1;
				} else{
					priceRes = generateRandom(price) + 1 + price;
				}
			}
			
			else{
				if(id%2 == 0){
					priceRes = generateRandom(price-1) + 1;
				} else{
					priceRes = generateRandom(price) + 1 + price;
				}
			}
		}
		
		return priceRes;
	}
}
