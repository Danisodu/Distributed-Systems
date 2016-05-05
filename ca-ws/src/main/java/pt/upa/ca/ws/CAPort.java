package pt.upa.ca.ws;

import java.lang.*;
import java.io.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.io.BufferedReader;



import javax.xml.ws.Endpoint;


import javax.jws.WebService;
import javax.xml.registry.JAXRException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
//import pt.upa.transporter.ws.BadJobFault_Exception;
//import pt.upa.transporter.ws.BadLocationFault_Exception;
//import pt.upa.transporter.ws.BadPriceFault_Exception;
//import pt.upa.transporter.ws.JobStateView;
//import pt.upa.transporter.ws.JobView;
//import pt.upa.transporter.ws.cli.TransporterClient;
//import pt.upa.transporter.ws.cli.TransporterClientException;

@WebService(
	    endpointInterface="pt.upa.ca.ws.CAPortType",
	    wsdlLocation="ca.1_0.wsdl",
	    name="UpaCA",
	    portName="CAPort",
	    targetNamespace="http://ws.ca.upa.pt/",
	    serviceName="CAService"
	)
public class CAPort implements CAPortType{

	public CAPort(){}
	/*
	public void initHandlersSearch(){
		
		//System.out.printf("Contacting UDDI to find Transporters...\n\n");
		UDDINaming uddiNaming = null;
		String uddiURL = "http://localhost:9090";
		
		try {
			uddiNaming = new UDDINaming(uddiURL);
		
			Collection<String> endpointAddresses = uddiNaming.list("UpaTransporter%");
			ArrayList<String> urls = (ArrayList<String>) endpointAddresses;
			
			for (String url: urls){
				
				System.out.println(url);
				TransporterClient clientHandler = null;
				
				try {
					clientHandler = new TransporterClient(url);
				    addClientHandler(clientHandler);  
				} catch (TransporterClientException e) {
					e.printStackTrace();
				}
								
			    // Here, every transporterClient stays with its own transporterServer
			}
			
		} catch (JAXRException e) {
			e.printStackTrace();
		}
	}

	*/
	
	
	public String ping(String name) {
		
		String pong = "Hello";
		
		
		return pong;
	}


	


	public static PublicKey getPublicKey(String name) throws Exception {

		Class cls = Class.forName("CAPort");
		ClassLoader cLoader = cls.getClassLoader();
		
		InputStream file = cLoader.getResourceAsStream(name);
		BufferedReader bf = new BufferedReader(new InputStreamReader(file));
		byte[] content = new byte[file.available()];

		X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(content);
		KeyFactory keyFacPub = KeyFactory.getInstance("RSA");
		PublicKey pub = keyFacPub.generatePublic(pubSpec);

		return pub;
	}

	

	
	
}

