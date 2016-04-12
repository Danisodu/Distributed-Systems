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
		
		JobView j = transporterClient.requestJob("Lisboa","Castelo Branco",14);	
				
		System.out.println(j.getCompanyName());
		System.out.println(j.getJobDestination());
		System.out.println(j.getJobOrigin());
		System.out.println(j.getJobPrice());
		System.out.println(j.getJobState().name());
		
		j = transporterClient.jobStatus("0");
		
		System.out.println(j.getCompanyName());
		System.out.println(j.getJobDestination());
		System.out.println(j.getJobOrigin());
		System.out.println(j.getJobPrice());
		System.out.println(j.getJobIdentifier());
		System.out.println(j.getJobState().name());
		
		j = transporterClient.decideJob("0", true);
				
		System.out.println(j.getJobIdentifier());
		System.out.println(j.getJobState().name());
		
		j = transporterClient.jobStatus("0");
		
		System.out.println(j.getJobIdentifier());
		System.out.println(j.getJobState().name());
		
		j = transporterClient.jobStatus("0");
		
		System.out.println(j.getJobIdentifier());
		System.out.println(j.getJobState().name());
		
		j = transporterClient.jobStatus("0");
		
		System.out.println(j.getJobIdentifier());
		System.out.println(j.getJobState().name());
		
		j = transporterClient.jobStatus("0");
		
		System.out.println(j.getJobIdentifier());
		System.out.println(j.getJobState().name());
		
	}

}
