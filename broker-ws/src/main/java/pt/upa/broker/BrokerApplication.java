package pt.upa.broker;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.EndpointManager;
import pt.upa.broker.ws.FaultManager;

public class BrokerApplication {

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
		
		
		try{
			System.out.printf("Contacting UDDI at %s%n", uddiURL);
			UDDINaming uddiNaming;
			uddiNaming = new UDDINaming(uddiURL);
			System.out.printf("Looking for '%s'%n", args[1]);
			urlAux = uddiNaming.lookup(args[1]);
		} catch(Exception e){ e.printStackTrace();}
		
		if (urlAux == null) {
			endpointManager.publish();
		} else{
			FaultManager thread = new FaultManager(url);
			thread.start();
			thread.join();
		}
	}

}
