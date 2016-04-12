package pt.upa.broker;

import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnknownTransportFault_Exception;
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
		
		System.out.println(brokerClient.requestTransport("Lisboa", "Setúbal", 14) + "\n");
		TransportView view = null;
		
		try{
			view = brokerClient.viewTransport("3");
		}
		catch(UnknownTransportFault_Exception e){ System.out.println(e.getMessage());
		
		}
		System.out.println(view.getId());
		System.out.println(view.getOrigin());
		System.out.println(view.getDestination());
		System.out.println(view.getTransporterCompany());
		System.out.println(view.getPrice());
		System.out.println(view.getState().name()+"\n");
		
		try{
			view = brokerClient.viewTransport("1");
		}
		catch(UnknownTransportFault_Exception e){ System.out.println(e.getMessage());
		
		}
		System.out.println(view.getId());
		System.out.println(view.getOrigin());
		System.out.println(view.getDestination());
		System.out.println(view.getTransporterCompany());
		System.out.println(view.getPrice());
		System.out.println(view.getState().name()+"\n");
		
		
		try{
			view = brokerClient.viewTransport("0");
		}
		catch(UnknownTransportFault_Exception e){ System.out.println(e.getMessage());
		
		}
		System.out.println(view.getId());
		System.out.println(view.getOrigin());
		System.out.println(view.getDestination());
		System.out.println(view.getTransporterCompany());
		System.out.println(view.getPrice());
		System.out.println(view.getState().name());
		
		
		try{
			view = brokerClient.viewTransport("0");
			System.out.println(view.getState().name());
		}
		catch(UnknownTransportFault_Exception e){ System.out.println(e.getMessage());
		
		}
		
		try{
			view = brokerClient.viewTransport("0");
			System.out.println(view.getState().name());
		}
		catch(UnknownTransportFault_Exception e){ System.out.println(e.getMessage());
		
		}		
			
		System.out.println(view.getState().name());	
		
		try{
			view = brokerClient.viewTransport("0");
			System.out.println(view.getState().name());	
		}
		catch(UnknownTransportFault_Exception e){ System.out.println(e.getMessage());
		
		}
				
		try{
			view = brokerClient.viewTransport("0");
			System.out.println(view.getState().name());	
		}
		catch(UnknownTransportFault_Exception e){ System.out.println(e.getMessage());}
		
		
		try{
			view = brokerClient.viewTransport("0");
			System.out.println(view.getState().name());	
		}
		catch(UnknownTransportFault_Exception e){ System.out.println(e.getMessage());}
		
		
		try{
			view = brokerClient.viewTransport("0");
			System.out.println(view.getState().name());	
		}
		catch(UnknownTransportFault_Exception e){ System.out.println(e.getMessage());}
		
		
		brokerClient.clearTransports();
		
		try{
			view = brokerClient.viewTransport("0");
		}
		catch(UnknownTransportFault_Exception e){ System.out.println(e.getMessage());
		
		}
	}

}
