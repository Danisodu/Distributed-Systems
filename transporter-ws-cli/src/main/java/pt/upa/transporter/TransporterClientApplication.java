package pt.upa.transporter;

import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

public class TransporterClientApplication {

	public static void main(String[] args) throws Exception {
				
		// Check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL name%n", TransporterClient.class.getName());
			return;
		}

		String uddiURL = args[0];
		String serviceName = args[1];
		
		TransporterClient transporterClient = new TransporterClient(uddiURL, serviceName);
		
		transporterClient.find();
		
		
		JobView j = transporterClient.requestJob("Lisboa","Castelo Branco",14);
		
		System.out.println(j.getJobDestination());
		//System.out.println(j.getJobState().value());
	
	}

}
