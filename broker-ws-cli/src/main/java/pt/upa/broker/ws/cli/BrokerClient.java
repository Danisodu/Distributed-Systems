package pt.upa.broker.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;

public class BrokerClient {
	
	//ter funções aqui, que depois fazem as repetivas no objeto?
	
	private String uddiURL;
	private String serviceName;
	
	public BrokerClient(String uURL){
		setUddiURL(uURL);
		setServiceName("UpaBroker"); //FIXME NO XML
	}
	
	public BrokerClient(){
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
		} catch (JAXRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		BrokerService service = new BrokerService();
		BrokerPortType port = service.getBrokerPort();

		System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);

		String result = port.ping("friend"); //FIXME
		System.out.println(result); 
		
	}

}
