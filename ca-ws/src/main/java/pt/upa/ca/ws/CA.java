package pt.upa.ca.ws;

import javax.jws.WebService;


@WebService
public interface CA {
	
	String ping(String name);
	
	String getPublicKey(String name) throws Exception;
		
//	byte[] makeDigitalSignature(Certificate certificate, PrivateKey privatekey) throws Exception;
	
}
