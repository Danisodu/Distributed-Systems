package pt.upa.transporter.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.jws.WebService;

@WebService(
	    endpointInterface="pt.upa.transporter.ws.TransporterPortType",
	    wsdlLocation="transporter.1_0.wsdl",
	    name="UpaTransporter",
	    portName="TransporterPort", 
	    targetNamespace="http://ws.transporter.upa.pt/",
	    serviceName="TransporterService"
	)
public class TransporterPort implements TransporterPortType{
	
	private int id;
	private String name;
	private List<JobView> jobs = new ArrayList<JobView>();
	private String[] centerTravels = {"Lisboa","Leiria","Santarém","Castelo Branco","Coimbra",
				"Aveiro","Viseu","Guarda"};

	public TransporterPort(String companyName){
		name = companyName;
		id = createIdByCompanyName();
	}
	
	@Override
	public String ping(String name) {
		return name;
	}

	@Override
	public JobView requestJob(String origin, String destination, int price)
			throws BadLocationFault_Exception, BadPriceFault_Exception {
		
		String[] travels;
		
		if(id%2==0){
			travels = new String[] {"Porto","Braga","Viana do Castelo","Vila Real","Bragança"};
		} else{
			travels = new String[] {"Setúbal","Évora","Portalegre","Beja","Faro"};
		}
		
		verifyLocations(origin, destination, travels);
		
		if(price > 100){ return null; }
		
		if(price <0){ 
			BadPriceFault fault = new BadPriceFault();
			fault.setPrice(price);
			throw new BadPriceFault_Exception("O preço tem que ser positivo",fault);
		}
		
		price = decidePrice(price);
		
		JobView newJv = createJob(name,""+jobs.size(),origin,destination,price,JobStateView.PROPOSED); 
				
		return newJv;
	}

	@Override
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		
		JobView job = getJobById(id);
		
		if(accept){ 
			Random rand = new Random();
			Timer timer = new Timer();
			long time = rand.nextInt(6) + 1;
			
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
				}
			}, time, 30);
		} else{
			job.setJobState(JobStateView.REJECTED);
		}
		
		return job;
	}

	@Override
	public JobView jobStatus(String id){
		
		JobView job = null;
		
		try{
			job = getJobById(id);
		} catch(BadJobFault_Exception e){ return null; }
				
		return job;
	}

	@Override
	public List<JobView> listJobs() {
		return jobs;
	}

	@Override
	public void clearJobs() {
		jobs.clear();
	}
	
	public void setTransporterIdentifier(int i){
		id = i;
	}

	public JobView getJob(int id) throws BadJobFault_Exception{
		
		JobView job = null;

		try{
			job = jobs.get(id);
		} 
		
		catch(IndexOutOfBoundsException e){ 
			BadJobFault fault = new BadJobFault();
			fault.setId(""+id); //change
			throw new BadJobFault_Exception("O id do job específicado não existe",fault); 
		}		
		
		return job;
	}
	
	public JobView getJobById(String id) throws BadJobFault_Exception{
		
		int index = Integer.parseInt(id);
		
		return getJob(index);
	}

	public void addJob(JobView job) {
		jobs.add(job);
	}
	
	public void removeJob(int index){
		jobs.remove(index);
	}
	
	public int createIdByCompanyName(){
		
		String id = name.replaceAll("\\D+","");
		
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
	
	public void verifyLocations(String origin, String destination, String[] travels) throws BadLocationFault_Exception{
		
		if(!(containsLocation(centerTravels,origin)) && !(containsLocation(travels, origin))){
			BadLocationFault fault = new BadLocationFault();
			fault.setLocation(origin);
			throw new BadLocationFault_Exception("Origem errada", fault);
		}
		
		else if(!(containsLocation(centerTravels,destination)) && !(containsLocation(travels, destination))){
			BadLocationFault fault = new BadLocationFault();
			fault.setLocation(destination);
			throw new BadLocationFault_Exception("Destino errado", fault);
		}
	}
	
	public boolean containsLocation(String[] vector, String name){
		
		for(String s: vector){
			if(s.equals(name)) return true;
		}
		
		return false;
	}

	public int decidePrice(int price){
		
		Random rand = new Random();
				
		if(price <= 10){
			price = rand.nextInt(price);
		}
		
		else if(10 < price || price == 100){
			if(price%3 == 0){
				if(id%3 == 0){
					price = rand.nextInt(price);
				} else{
					price = rand.nextInt(price) + 1 + rand.nextInt(6);
				}
			}
			
			else{
				if(id%3 == 0){
					price = rand.nextInt(price) + 1 + rand.nextInt(6);
				} else{
					price = rand.nextInt(price);
				}
			}
		}
		
		return price;
	}
}
