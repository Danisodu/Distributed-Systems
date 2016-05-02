package pt.upa.ca;

import pt.upa.ca.ws.CAPort;
import java.util.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;


//import pt.upa.ca.ws.EndpointManager;
public class CAApplication {


public static void main(String[] args) throws Exception {

		//final String publicKeyPathBroker = "/broker-ws/target/";
		//final String privateKeyPathBroker =" /broker-ws/target/" ;
		//final String publicKeyPathTransporter = "/transporter-ws/";
		//final String privateKeyPathTransporter = "/transporter-ws/target/";
		final String privateKeyPath = "./CAprivatekey.txt";
		final String publicKeyPath= "./CApublickey.txt";

	
		System.out.println("Generate and save keys");
		//write(publicKeyPathBorker, privateKeyPathBroker);
		//write(publicKeyPathTransporter, privateKeyPathTransporter);
		write(publicKeyPath, privateKeyPath);
		
	
	
	}

}

/*package pt.upa.broker;

import pt.upa.broker.ws.EndpointManager;

public class BrokerApplication {

	public static void main(String[] args) throws Exception {


			
		System.out.println(BrokerApplication.class.getSimpleName() + " starting...");
		
		// Check arguments
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", BrokerApplication.class.getName());
			return;
		}

		String uddiURL = args[0];
		String name = args[1];
		String url = args[2];
		
		EndpointManager endpointManager = new EndpointManager(uddiURL,name,url);
		
		endpointManager.publish();
			
	}

} */
