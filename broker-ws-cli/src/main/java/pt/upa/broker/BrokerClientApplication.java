package pt.upa.broker;

import java.util.List;

import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
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
		/*
		System.out.println("Do ping!\n");
		System.out.println(brokerClient.ping("hello"));
		
		System.out.println("Request transport!\n");
		
		try{
			System.out.println(brokerClient.requestTransport("Lisboa", "Beja", 50));
		}
		catch( UnavailableTransportFault_Exception e){e.getMessage();}
		catch( UnavailableTransportPriceFault_Exception e){e.getMessage();}
		catch( UnknownLocationFault_Exception e){e.getMessage();}
		catch( InvalidPriceFault_Exception e){e.getMessage();}

		*/
		System.out.println("Viewing created transport!\n");
		TransportView v = null;
		
		
		v = brokerClient.viewTransport("0");
		

		
		System.out.println(v.getId());
		System.out.println(v.getOrigin());
		System.out.println(v.getDestination());
		System.out.println(v.getTransporterCompany());
		System.out.println(v.getPrice());
		System.out.println(v.getState().name()+"\n");
		
		System.out.println("Viewing created transport!\n");
		
		TransportView v1 = brokerClient.viewTransport("3");

		
		System.out.println(v1.getId());
		System.out.println(v1.getOrigin());
		System.out.println(v1.getDestination());
		System.out.println(v1.getTransporterCompany());
		System.out.println(v1.getPrice());
		System.out.println(v1.getState().name()+"\n");
		
		
		System.out.println("Clear transports!\n");

		brokerClient.clearTransports();
			
		System.out.println("List transports\n");
		List<TransportView> list = brokerClient.listTransports();

		list = brokerClient.listTransports();
		
		for(TransportView f: list){
			System.out.println(f.getId());
			System.out.println(f.getOrigin());
			System.out.println(f.getDestination());
			System.out.println(f.getTransporterCompany());
			System.out.println(f.getPrice());
			System.out.println(f.getState().name()+"\n");
		}
		
		/*
		System.out.println("Request another transport!\n");
		
		try{
			System.out.println(brokerClient.requestTransport("Faro", "Lisboa", 40));
		}
		catch( UnavailableTransportFault_Exception e){e.getMessage();}
		catch( UnavailableTransportPriceFault_Exception e){e.getMessage();}
		catch( UnknownLocationFault_Exception e){e.getMessage();}
		catch( InvalidPriceFault_Exception e){e.getMessage();}
		
		System.out.println("List transports!\n");
		List<TransportView> list = brokerClient.listTransports();
	
		for(TransportView t: list){
			System.out.println(t.getId());
			System.out.println(t.getOrigin());
			System.out.println(t.getDestination());
			System.out.println(t.getTransporterCompany());
			System.out.println(t.getPrice());
			System.out.println(t.getState().name()+"\n");
		}
		
		
		TransportView v = null;
		
		try{
			v = brokerClient.viewTransport("3");
		}
		catch(UnknownTransportFault_Exception e){ System.out.println(e.getMessage());
		
		}
		
		System.out.println(v.getId());
		System.out.println(v.getOrigin());
		System.out.println(v.getDestination());
		System.out.println(v.getTransporterCompany());
		System.out.println(v.getPrice());
		System.out.println(v.getState().name()+"\n");
		
		System.out.println("Clear transports!\n");

		brokerClient.clearTransports();
			
		System.out.println("List transports\n");
		List<TransportView> list = brokerClient.listTransports();

		list = brokerClient.listTransports();
		
		for(TransportView f: list){
			System.out.println(f.getId());
			System.out.println(f.getOrigin());
			System.out.println(f.getDestination());
			System.out.println(f.getTransporterCompany());
			System.out.println(f.getPrice());
			System.out.println(f.getState().name()+"\n");
		}*/
		
	}

}

