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
		
		System.out.println(brokerClient.ping(":jj)"));
		
		try{
			System.out.println(brokerClient.requestTransport("Lisboa", "Beja", 51));
		}
		catch( UnavailableTransportFault_Exception e){e.getMessage();}
		catch( UnavailableTransportPriceFault_Exception e){e.getMessage();}
		catch( UnknownLocationFault_Exception e){e.getMessage();}
		catch( InvalidPriceFault_Exception e){e.getMessage();}
		
		
		TransportView view = null;
		
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
		
		List<TransportView> list = brokerClient.listTransports();
	
		for(TransportView v: list){
			System.out.println(v.getId());
			System.out.println(v.getOrigin());
			System.out.println(v.getDestination());
			System.out.println(v.getTransporterCompany());
			System.out.println(v.getPrice());
			System.out.println(v.getState().name()+"\n");
		}
		
		/*
		try{
			view = brokerClient.viewTransport("2");
		}
		catch(UnknownTransportFault_Exception e){ System.out.println(e.getMessage());
		
		}
		
		System.out.println(view.getId());
		System.out.println(view.getOrigin());
		System.out.println(view.getDestination());
		System.out.println(view.getTransporterCompany());
		System.out.println(view.getPrice());
		System.out.println(view.getState().name()+"\n");
		
		/*
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
		}
		catch(UnknownTransportFault_Exception e){ System.out.println(e.getMessage());*/
		
	}

}
