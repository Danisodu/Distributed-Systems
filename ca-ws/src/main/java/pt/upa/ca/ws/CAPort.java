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
import java.io.BufferedReader;

import javax.jws.WebService;
import javax.xml.registry.JAXRException;

import pt.upa.ca.ws.CAPortType;

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
	
	public String ping(String name) {
		
		String pong = "Hello";
		
		return pong;
	}

	////////// Estes metódos têm que ser gerados automaticamente pelo wsdl////////////////
	////////// Enquanto não estiverem não vai funcionar, portanto testem à parte /////////
	public static PublicKey getPublicKey(String name) throws Exception {

		Class cls = Class.forName("CAPort");
		ClassLoader cLoader = cls.getClassLoader();
		
		InputStream file = cLoader.getResourceAsStream(name);
		byte[] content = new byte[file.available()];

		X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(content);
		KeyFactory keyFacPub = KeyFactory.getInstance("RSA");
		PublicKey pub = keyFacPub.generatePublic(pubSpec);


		return pub;
	}

	//falta criar um certificado e enviar com assinatura


	public static byte[] makeDigitalSignature(Certificate certificate, PrivateKey privatekey) throws Exception{

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
	}	
}

