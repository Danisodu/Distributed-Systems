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

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.io.BufferedReader;

import javax.jws.WebService;

@WebService(endpointInterface = "pt.upa.ca.ws.CA")
public class CAImpl implements CA{

	public CAImpl(){}
	
	public String ping(String name) {
		return name;
	}	

	@Override
	public String requestCertificate(String name) throws Exception{

		Class cls = Class.forName("CAImpl");
		ClassLoader cLoader = cls.getClassLoader();

		String cert = name + ".cer";

		//isto implica que os certificados estao na resource do ca?
		InputStream file = cLoader.getResourceAsStream(cert);
		byte[] bcontent = new byte[file.available()];

		String content = printBase64Binary(bcontent);

		//file.read(content);
		//file.close();

		return content;
	}

	//falta criar um certificado e enviar com assinatura

	/*public byte[] makeDigitalSignature(Certificate certificate, PrivateKey privatekey) throws Exception {
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

