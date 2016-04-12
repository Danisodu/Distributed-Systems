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
	private List<TransporterJob> jobs = new ArrayList<TransporterJob>();
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
		
		//BadLocationFault_Exception
		verifyLocations(origin, destination, travels);
		
		if(price > 100){ return null; }
		
		if(price <0){ 
			BadPriceFault fault = new BadPriceFault();
			fault.setPrice(price);
			throw new BadPriceFault_Exception("O preço tem que ser positivo",fault);
		} //change
		
		price = decidePrice(price);
		
		//Nao tem que ser criado um novo TransporterJob...tem que ser retirado o transporterjob da list que esta transportadora tem??

		TransporterJob newTj = new TransporterJob(name,""+jobs.size(),origin,destination,price,JobState.PROPOSED); 
		//change
		
		addJob(newTj);
		
		return convertJob(newTj);
	}

	@Override
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		
		TransporterJob job = getJobById(id);  // o TransporterJob e um JobView, estamos a retirar a proposta que tem o id lança BadJobFault_Exception
		
		if(accept){ 
			Random rand = new Random();
			Timer timer = new Timer();
			long time = rand.nextInt(6) + 1;
			
			job.setState(JobState.ACCEPTED);
			timer.schedule( new TimerTask(){

				@Override
				public void run() {
					String state = job.getState().name();

					if(state.equals("ACCEPTED")){
						job.setState(JobState.HEADING);
					}
					
					else if(state.equals("HEADING")){
						job.setState(JobState.ONGOING);
					}
					
					else{
						job.setState(JobState.COMPLETED);
						timer.cancel();
					}
				}
			}, time, 30);
		} else{
			job.setState(JobState.REJECTED);
		}
		
		return convertJob(job);
	}

	@Override
	public JobView jobStatus(String id){
		
		TransporterJob job = null;
		
		try{
			job = getJobById(id);
		} catch(BadJobFault_Exception e){ return null; }
		
		JobView view = convertJob(job);
		
		return view;
	}

	@SuppressWarnings("null")
	@Override
	public List<JobView> listJobs() {
		
		List<JobView> list = null;
		
		for(TransporterJob jb: jobs){
			JobView jv = this.convertJob(jb);
			list.add(jv);
		}
	    
		return list;
	}

	@Override
	public void clearJobs() {
		jobs.clear();
	}

	public TransporterJob getJob(int id) throws BadJobFault_Exception{
		
		TransporterJob job = null;

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
	
	public TransporterJob getJobById(String id) throws BadJobFault_Exception{
		
		int index = Integer.parseInt(id);
		
		return getJob(index);
	}

	public void addJob(TransporterJob transport) {
		jobs.add(transport);
	}
	
	public void removeJob(int index){
		jobs.remove(index);
	}
	
	public int createIdByCompanyName(){
		
		String id = name.replaceAll("\\D+","");
		
		int idConverted = Integer.parseInt(id);		
		
		return idConverted;
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
	
	public JobView convertJob(TransporterJob job){
			    
	    JobView newJv = new JobView();
	    String state = job.getState().name();
	    
	    newJv.setCompanyName(job.getCompanyName());
	    newJv.setJobDestination(job.getDestination());
	    newJv.setJobIdentifier(job.getIdentifier());
	    newJv.setJobOrigin(job.getOrigin());
	    newJv.setJobPrice(job.getPrice());
	    newJv.setJobState(JobStateView.fromValue(state));
	    
	    return newJv;
	}

	public void setTransporterIdentifier(int identifier){
		id=identifier;
	}
	
}
