package pt.upa.ca;

import pt.upa.ca.ws.cli.CAClient;

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

        // the following remote invocations are just basic examples
        // the actual tests are made using JUnit

        System.out.println("Invoke ping()...");
        String result = client.ping("CA client");
        System.out.println(result);  

		//final String publicKeyPathBroker = "/broker-ws/target/";   
		//final String privateKeyPathBroker =" /broker-ws/target/" ;   na resource do broker
		//final String publicKeyPathTransporter = "/transporter-ws/";
		//final String privateKeyPathTransporter = "/transporter-ws/target/";   na resource do transporter

		//final String privateKeyPath = "./CAprivatekey.txt";
		//final String publicKeyPath= "./CApublickey.txt";

		//System.out.println("Generate and save keys");
		//write(publicKeyPathBorker, privateKeyPathBroker);
		//write(publicKeyPathTransporter, privateKeyPathTransporter);
		//client.write(publicKeyPath, privateKeyPath);
    }
}
