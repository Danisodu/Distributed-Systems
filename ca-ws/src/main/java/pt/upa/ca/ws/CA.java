package pt.upa.ca.ws;

import javax.jws.WebService;


@WebService
public interface CA {
	
	String ping(String name);
	
	String requestCertificate(String name); //get broker key
		
}
