package pt.upa.ca;

import pt.upa.ca.ws.cli.CAClient;

import java.security.PublicKey;
import java.security.cert.Certificate;


public class CAClientApplication {

    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length == 0) {
            System.err.println("Argument(s) missing!");
            System.err.println("Usage: java " + CAClientApplication.class.getName()
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
        CAClient client = null;

        if (wsURL != null) {
            System.out.printf("Creating client for server at %s%n", wsURL);
            client = new CAClient(wsURL);
        } else if (uddiURL != null) {
            System.out.printf("Creating client using UDDI at %s for server with name %s%n",
                uddiURL, wsName);
            client = new CAClient(uddiURL, wsName);
        }

        System.out.println("Invoke ping()...");
        String result = client.ping("CA client");
        System.out.println(result);  
        
        Certificate cert = client.GetCertificate("UpaBroker");
        
        PublicKey pc = cert.getPublicKey();

    

    }
}
