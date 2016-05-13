package pt.upa.broker;

import java.util.List;

import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.cli.BrokerClient;


public class BrokerClientApplication {

    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length == 0) {
            System.err.println("Argument(s) missing!");
            System.err.println("Usage: java " + BrokerClientApplication.class.getName()
                    + " wsURL OR uddiURL wsName");
            return;
        }
        String uddiURL = null;
        String wsName = null;
        String wsURL = null;
        if (args.length == 1) {
            wsURL = args[0];
        } else if (args.length >= 2) {
            uddiURL = args[0];
            wsName = args[1];
        }

        // Create client
        BrokerClient client = null;

        if (wsURL != null) {
            System.out.printf("Creating client for server at %s%n", wsURL);
            client = new BrokerClient(wsURL);
        } else if (uddiURL != null) {
            System.out.printf("Creating client using UDDI at %s for server with name %s%n",
                uddiURL, wsName);
            client = new BrokerClient(uddiURL, wsName);
        }

        // the following remote invocations are just basic examples
        // the actual tests are made using JUnit

        
        System.out.println("Ping...");

        System.out.println(client.ping("ohh"));

        System.out.println("List...");

        List<TransportView> list = client.listTransports();
                
        for(TransportView view: list){
            System.out.println(view.getId());
        }
        
        System.out.println("Request...");
        
        System.out.println(client.requestTransport("Leiria", "Lisboa", 31));
        
        System.out.println("List...");

        list = client.listTransports();
        
        for(TransportView view: list){
            System.out.println(view.getId());
        }
    }
    
}
