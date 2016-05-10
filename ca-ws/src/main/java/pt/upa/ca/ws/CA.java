package pt.upa.ca.ws;

import javax.jws.WebService;


@WebService
public interface CA {
	
	String ping(String name);
	
	String getPublicKey(String name) throws Exception; //get broker key
		
	//get transproter key(int )
//	byte[] makeDigitalSignature(Certificate certificate, PrivateKey privatekey) throws Exception;
	
}
