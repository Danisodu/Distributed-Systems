package pt.upa.broker;


import java.util.Collection;

import javax.xml.registry.JAXRException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPort;
import pt.upa.broker.ws.EndpointManager;
import pt.upa.broker.ws.FaultManager;

public class BrokerApplication {
	
	public static void remove(String args){
		System.out.printf("Contacting UDDI at %s%n", "http://localhost:9090");
		UDDINaming uddiNaming = null;
		try {
			uddiNaming = new UDDINaming("http://localhost:9090");
		} catch (JAXRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Collection<String> list = null;
		try {
			list = uddiNaming.list(args);
		} catch (JAXRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(list.size() + "\n");
		System.out.printf("Looking for '%s'%n", args);	
		try {
			uddiNaming.unbind(args);
		} catch (JAXRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	public static void main(String[] args) throws Exception {
			
		System.out.println(BrokerApplication.class.getSimpleName() + " starting...");
		
		// Check arguments
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", BrokerApplication.class.getName());
			return;
		}

		String uddiURL = args[0];
		String name = args[1];
		String url = args[2];
		String urlAux = null;
				
		EndpointManager endpointManager = new EndpointManager(uddiURL,name,url); 

//		remove(name);
		
		try{
			System.out.printf("Contacting UDDI at %s%n", uddiURL);
			UDDINaming uddiNaming = new UDDINaming(uddiURL);
			System.out.printf("Looking for '%s'%n", name);
//			uddiNaming.unbind("UpaBrokerBackUp");
			urlAux = uddiNaming.lookup(name);			
		} catch(Exception e){ e.printStackTrace(); }


		if (urlAux != null) {
			endpointManager.setServiceName("UpaBrokerBackUp");
			endpointManager.setServiceURL("http://localhost:8079/broker-ws/endpoint");
			BrokerPort b = endpointManager.start(2);
			FaultManager f = new FaultManager(url);
			f.runVerification();
			System.out.println("Publishing secondary broker as primary");
			endpointManager.stop();
			endpointManager.setServiceName(name);
			endpointManager.setServiceURL(url);
			endpointManager.startExistingBroker(b);
		} else {
			endpointManager.start(1);
		}
		
		endpointManager.awaitConnections();
		endpointManager.stop();
		
		System.out.print("Finished process");
	}

}
