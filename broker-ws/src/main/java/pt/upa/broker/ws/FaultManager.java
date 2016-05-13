package pt.upa.broker.ws;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Map;

import javax.xml.ws.BindingProvider;

public class FaultManager{

	private String wsPrimaryURL;
	
	public FaultManager(String url){
		setWsPrimaryURL(url);
	}

	public String getWsPrimaryURL() {
		return wsPrimaryURL;
	}

	public void setWsPrimaryURL(String wsPrimURL) {
		wsPrimaryURL = wsPrimURL;
	}
	
	public BrokerPortType getPrimaryBroker(){
		System.out.println("Setting primary broker ...");
		BrokerService service = new BrokerService();
		BrokerPortType port = service.getBrokerPort();
		System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, wsPrimaryURL);
		return port;
	}
	
	public void runVerification() {
	
		BrokerPortType primaryBroker = getPrimaryBroker();
		String ping;
		
		primaryBroker.update(new ArrayList<TransportView>());
		
		do{
			try{
				ping = primaryBroker.ping("I'm Alive!");
				System.out.println("Primary broker is alive!");
				Thread.sleep(4000);
			} catch(Exception e){
				ping = null;
				System.out.println("\nPrimary Broker detected not alive\n");
			}
		} while(ping != null);
		
	}
}
