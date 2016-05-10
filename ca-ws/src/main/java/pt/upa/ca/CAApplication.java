package pt.upa.ca;

import pt.upa.ca.ws.EndpointManager;

public class CAApplication {

	public static void main(String[] args) throws Exception {

		System.out.println(CAApplication.class.getSimpleName() + " starting...");
		
		// Check arguments
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", CAApplication.class.getName());
			return;
		}

		String uddiURL = args[0];
		String name = args[1];
		String url = args[2];
		
		EndpointManager endpointManager = new EndpointManager(uddiURL,name,url);
		
		endpointManager.publish();
	}

}