package pt.upa.ca.ws;

import java.lang.*;
import java.io.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;


import javax.jws.WebService;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;

@WebService(endpointInterface = "pt.upa.ca.ws.CA")
public class CAImpl implements CA{

	public CAImpl(){}
	
	public String ping(String name) {

		String pong = "Hello";
		
		return pong;
	}

	@Override
	public String requestCertificate(String name) {

		Class cls = null;
		
		try {
			cls = Class.forName("pt.upa.ca.ws.CAImpl");
		} catch (ClassNotFoundException e) {
		
			return null;
		}

		ClassLoader cLoader = cls.getClassLoader();
		String cert = name + ".cer";
		InputStream file = cLoader.getResourceAsStream(cert);
	
		byte[] bcontent = null;
		try {
			bcontent = new byte[file.available()];
			file.read(bcontent);
			
		} catch (IOException e) {
			
			return null;
		}

		String content = printBase64Binary(bcontent);

		return content;
	}

}

