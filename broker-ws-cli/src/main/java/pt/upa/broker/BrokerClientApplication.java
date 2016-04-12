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
		String serviceName = args[1];
		
		BrokerClient brokerClient = new BrokerClient(uddiURL,serviceName);
		
		System.out.println(brokerClient.requestTransport("Lisboa", "Castelo Branco", 14));
		
	}

}
