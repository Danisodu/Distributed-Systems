package pt.upa.broker.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.List;
import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;


public class BrokerClient{
		
	private String uddiURL;
	private String serviceName;
	private BrokerPortType service;
	
	public BrokerClient(String uURL, String serviceName){
		setUddiURL(uURL);
		setServiceName(serviceName);
		initServiceSearch();
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
	
	public void initServiceSearch(){

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
		BrokerService serviceBs = new BrokerService();
		BrokerPortType port = serviceBs.getBrokerPort();

		service = port;
		
		System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider = (BindingProvider) port;	
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);		
	}

	public String ping(String name) {
		return service.ping(name);
	}

	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, 
			UnknownLocationFault_Exception {
		return service.requestTransport(origin, destination, price);
	}

	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		return service.viewTransport(id);
	}

	public List<TransportView> listTransports() {
		return service.listTransports();
	}

	public void clearTransports() {
		service.clearTransports();
	}

}
