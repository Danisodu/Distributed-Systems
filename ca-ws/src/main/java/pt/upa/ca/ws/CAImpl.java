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

import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.jws.WebService;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;

@WebService(endpointInterface = "pt.upa.ca.ws.CA")
public class CAImpl implements CA{

	public CAImpl(){}
	
	public String ping(String name) {

<<<<<<< HEAD
=======
		
		String pong = "Hello";
		
		
		return pong;
	}

>>>>>>> master
	@Override
	public String requestCertificate(String name) throws Exception{

		Class cls = Class.forName("CAImpl");
<<<<<<< HEAD
=======
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

	/*private static byte[] readFile(String path) throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(path);
		byte[] content = new byte[fis.available()];
		fis.read(content);
		fis.close();
		return content;
	}
*/

	//os certificados estao nas resources com os nomes das entidades respetivas e estão assinados pela CA
	


	/*public byte[] requestCertificate2(String name){

		String cert = name + ".cer";

		//byte[] certificate = readFile(publicKeyPath); o publicKeyPath nao esta definido

		//return certificate;
		return null;

	}*/

	/*@Override
	public String getPublicKey(String name) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}*/




	/*public static PublicKey getPublicKeyFromCertificate(Certiticate cer){
		return cer.getPublicKey();
	}*/

    



	/*public String getPublicKey(String name) throws Exception {
		
		Class cls = Class.forName("CAPort");
>>>>>>> master
		ClassLoader cLoader = cls.getClassLoader();

		String cert = name + ".cer";

<<<<<<< HEAD
		//isto implica que os certificados estao na resource do ca?
		InputStream file = cLoader.getResourceAsStream(cert);
		byte[] bcontent = new byte[file.available()];

		String content = printBase64Binary(bcontent);

		//file.read(content);
		//file.close();

		return content;
=======


		
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

		return name;
>>>>>>> master
	}


	}*/
	

	/*public static byte[] makeDigitalSignature(byte[] bytes, PrivateKey privatekey) throws Exception{
		Signature sig = Signature.getInstance("SHA1WithRSA");
		sig.initSign(privatekey);
		sig.update(bytes);
		byte[] signature = sig.sign();

		return signature;
	}*/


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

