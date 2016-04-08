package pt.upa.transporter.ws;

import java.util.List;
import java.util.Random;
import java.util.Vector;

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
	private List<TransporterJob> jobs = new Vector<TransporterJob>();
	private String[] centerTravels = {"Lisboa","Leiria","Santarém","Castelo Branco","Coimbra",
				"Aveiro","Viseu","Guarda"};

	public TransporterPort(int i, String companyName){
		
		id = i;
		name = companyName;
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
		} //change eventually
		
		price = decidePrice(price);
		
		TransporterJob newTj = new TransporterJob(name,""+jobs.size(),origin,destination,price,JobState.PROPOSED);
		//change size

		addTransport(newTj);
		
		return createJobView(origin, destination, price);
	}

	@Override
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		return null;
	}

	@Override
	public JobView jobStatus(String id) throws IndexOutOfBoundsException{
		
		int index = Integer.parseInt(id);
		TransporterJob job = null;
		
		try{
			job = getTransport(index);
		} catch(IndexOutOfBoundsException e){ return null; }
		
		JobView view = createJobView(job.getOrigin(), job.getDestination(), job.getPrice());
		
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
		
		for(int index = 0; index < jobs.size(); index++){
			removeTransport(index);
		}
	}

	public TransporterJob getTransport(int id) {
		return jobs.get(id);
	}

	public void addTransport(TransporterJob transport) {
		jobs.add(transport);
	}
	
	public void removeTransport(int index){
		jobs.remove(index);
	}
	
	public void verifyLocations(String origin, String destination, String[] travels) throws BadLocationFault_Exception{
		
		if(!(containsLocation(centerTravels,origin) || containsLocation(travels, origin))){
			BadLocationFault fault = new BadLocationFault();
			fault.setLocation(origin);
			throw new BadLocationFault_Exception("Origem errada", fault);
		}
		
		else if(!(containsLocation(centerTravels,destination) || containsLocation(travels, destination))){
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

	public JobView createJobView(String origin, String destination, int price){
		
		JobView jv = new JobView();
		int numJobs = jobs.size();
		
		jv.setCompanyName(name);
		jv.setJobIdentifier("" + numJobs++); //change
		jv.setJobOrigin(origin);
		jv.setJobDestination(destination);
		jv.setJobPrice(price);
		
		return jv;
	}
	
	public JobView convertJob(TransporterJob t){
			    
	    JobView newJv = new JobView();
	    String state = t.getState().value();
	    
	    newJv.setCompanyName(t.getCompanyName());
	    newJv.setJobDestination(t.getDestination());
	    newJv.setJobIdentifier(t.getIdentifier());
	    newJv.setJobOrigin(t.getOrigin());
	    newJv.setJobPrice(t.getPrice());
	    newJv.setJobState(JobStateView.fromValue(state));
	    
	    return newJv;
	}

}
