package pt.upa.ca.ws;

import java.lang.*;
import java.io.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import java.io.BufferedReader;



import javax.xml.ws.Endpoint;


import javax.jws.WebService;
import javax.xml.registry.JAXRException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;


@WebService(
	    endpointInterface="pt.upa.ca.ws.CAPortType",
	    wsdlLocation="ca.1_0.wsdl",
	    name="UpaCA",
	    portName="CAPort",
	    targetNamespace="http://ws.ca.upa.pt/",
	    serviceName="CAService"
	)
public class CAPort implements CAPortType{

	private String publicKeyPath = "/main/resources/";

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

	private static byte[] readFile(String path) throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(path);
		byte[] content = new byte[fis.available()];
		fis.read(content);
		fis.close();
		return content;
	}


	//os certificados estao nas resources com os nomes das entidades respetivas e estão assinados pela CA
	public byte[] requestCertificate(String name){

		Class cls = Class.forName("CAPort");
		ClassLoader cLoader = cls.getClassLoader();

		String cert = name + ".cer";
		InputStream file = cLoader.getResourceAsStream(cert);
		byte[] content = new byte[file.available()];

		file.read(content);
		file.close();

		return content;
	}



	public byte[] requestCertificate2(String name){

		String cert = name + ".cer";

		byte[] certificate = readFile(publicKeyPath);

		return certificate;

	}




	/*public static PublicKey getPublicKeyFromCertificate(Certiticate cer){
		return cer.getPublicKey();
	}*/

    

	/*public PublicKey getPublicKey(String name) throws Exception {


		Class cls = Class.forName("CAPort");
		ClassLoader cLoader = cls.getClassLoader();
		
		InputStream file = cLoader.getResourceAsStream(name);
		byte[] content = new byte[file.available()];

		X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(content);
		KeyFactory keyFacPub = KeyFactory.getInstance("RSA");
		PublicKey pub = keyFacPub.generatePublic(pubSpec);


		return pub;
	}*/

	
	/*public static boolean verifyDigitalSignature(byte[] cipherDigest, byte[] bytes, PublicKey publicKey) throws Exception{
		Signature sig = Signature.getInstance("SHA1WithRSA");
		sig.initVerify(publicKey);
		sig.update(bytes);
		try{
			return sig.verify(cipherDigest);
		}catch(SignatureException se){
			System.err.println("Caught exception while verifying signature" + se);
			return false;
		}

	}*/
	

	/*public static byte[] makeDigitalSignature(byte[] bytes, PrivateKey privatekey) throws Exception{
		Signature sig = Signature.getInstance("SHA1WithRSA");
		sig.initSign(privatekey);
		sig.update(bytes);
		byte[] signature = sig.sign();

		return signature;
	}*/

	/*public static byte[] makeDigitalSignature(Certificate certificate, PrivateKey privatekey) throws Exception{

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(certificate);
    	byte[] bytes = out.toByteArray(); 
			//passar o certificado para bytes
		Signature sig = Signature.getInstance("SHA1WithRSA");
		sig.initSign(privatekey);
		sig.update(bytes);
		byte[] signature = sig.sign();

		return signature;
	}*/

	

	
	
}

