package pt.upa.transporter.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.List;
import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.TransporterService;

public class TransporterClient{
	
	//tem que ter aqui funcoes que chamam os do TransporterServer
	//como fazer look up de todos os serviços existentes?
	//e dps chamá-los dentro 
	
	private String uddiURL;
	private String serviceName;
	private TransporterPortType handler;
	
	public TransporterClient(String uURL, String sName){
		uddiURL = uURL;
		serviceName = sName;
	}
	
	public TransporterClient(){
	}

	public String getUddiURL() {
		return uddiURL;
	}

	public void setUddiURL(String uddiURL) {
		this.uddiURL = uddiURL;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public void find(){
		
		System.out.printf("Contacting UDDI at %s%n", uddiURL);
		UDDINaming uddiNaming = null;
		
		try {
			uddiNaming = new UDDINaming(uddiURL);
		} catch (JAXRException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.printf("Looking for '%s'%n", serviceName);
		String endpointAddress = null;
		
		try {
			endpointAddress = uddiNaming.lookup(serviceName);
		} catch (JAXRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (endpointAddress == null) {
			System.out.println("Not found!");
			return;
		} else {
			System.out.printf("Found %s%n", endpointAddress);
		}

		System.out.println("Creating stub ...");
		TransporterService service = new TransporterService();
		TransporterPortType port = service.getTransporterPort();
		
		handler = port;

		System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);

	}
	
	public String ping(String name) {
		return handler.ping(name);
	}
	
	public JobView requestJob(String origin, String destination, int price)
			throws BadLocationFault_Exception, BadPriceFault_Exception {
		return handler.requestJob(origin, destination, price);
	}
	
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		return handler.decideJob(id, accept);
	}
	
	public JobView jobStatus(String id) {
		return handler.jobStatus(id);
	}
	
	public List<JobView> listJobs() {
		return handler.listJobs();
	}
	
	public void clearJobs() {
		handler.clearJobs();
	}
	
}
