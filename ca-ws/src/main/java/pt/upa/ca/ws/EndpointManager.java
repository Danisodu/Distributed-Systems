package pt.upa.ca.ws;

import javax.xml.ws.Endpoint;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

public class EndpointManager {
	
	private String uddiURL;
	private String serviceName;
	private String serviceURL;
	
	public EndpointManager(String uURL, String name, String url){
		setUddiURL(uURL);
		setServiceName(name);
		setServiceURL(url);
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

	public String getServiceURL() {
		return serviceURL;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}

	public void publish(){
		
		Endpoint endpoint = null;
		UDDINaming uddiNaming = null;

		try {
			endpoint = Endpoint.create(new CAImpl());

			// publish endpoint
			System.out.printf("Starting %s%n", serviceURL);
			endpoint.publish(serviceURL);

			// publish to UDDI
			System.out.printf("Publishing '%s' to UDDI at %s%n", serviceName, uddiURL);
			uddiNaming = new UDDINaming(uddiURL);
			uddiNaming.rebind(serviceName, serviceURL);

			// wait
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");			
			System.in.read();

		} catch (Exception e) {
			System.out.printf("Caught exception: %s%n", e);
			e.printStackTrace();

		} finally {
			try {
				if (endpoint != null) {
					// stop endpoint
					endpoint.stop();
					System.out.printf("Stopped %s%n", serviceURL);
				}
			} catch (Exception e) {
				System.out.printf("Caught exception when stopping: %s%n", e);
			}
			try {
				if (uddiNaming != null) {
					// delete from UDDI
					uddiNaming.unbind(serviceName);
					System.out.printf("Deleted '%s' from UDDI%n", serviceName);
				}
			} catch (Exception e) {
				System.out.printf("Caught exception when deleting: %s%n", e);
			}
		}
	}
	
}
