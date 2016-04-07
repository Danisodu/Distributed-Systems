package pt.upa.broker;

import pt.upa.broker.ws.cli.BrokerClient;

public class BrokerClientApplication {

	public static void main(String[] args) throws Exception {
		
		// Check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL name%n", BrokerClient.class.getName());
			return;
		}
		
		String uddiURL = args[0];
		String name = args[1];
		
		BrokerClient brokerClient = new BrokerClient(uddiURL);
		
		brokerClient.find();

	}

}
