package pt.upa.transporter;

import pt.upa.transporter.ws.cli.TransporterClient;

public class TransporterClientApplication {

	public static void main(String[] args) throws Exception {
				
		// Check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL name%n", TransporterClient.class.getName());
			return;
		}

		String uddiURL = args[0];
		
		TransporterClient transporterClient = new TransporterClient(uddiURL);
		
		transporterClient.find();
	
	}

}
