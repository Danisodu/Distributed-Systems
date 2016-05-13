package pt.upa.ca.ws.cli;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.io.ByteArrayInputStream;
<<<<<<< HEAD
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.security.Certificate;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
=======
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
>>>>>>> master
import java.util.Map;

import pt.upa.ca.ws.CA;
import pt.upa.ca.ws.CAImplService;
import pt.upa.ca.ws.Exception_Exception;

import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;


public class CAClient implements CA{
	/** WS service */
	CAImplService service = null;

	/** WS port (port type is the interface, port is the implementation) */
	CA port = null;

	/** UDDI server URL */
	private String uddiURL = null;

	/** WS name */
	private String wsName = null;

	/** WS endpoint address */
	private String wsURL = null; // default value is defined inside WSDL

	public String getWsURL() {
		return wsURL;
	}

	/** output option **/
	private boolean verbose = true;

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/** constructor with provided web service URL */
	public CAClient(String wsURL) throws CAClientException {
		this.wsURL = wsURL;
		createStub();
	}

	/** constructor with provided UDDI location and name */
	public CAClient(String uddiURL, String wsName) throws CAClientException {
		this.uddiURL = uddiURL;
		this.wsName = wsName;
		uddiLookup();
		createStub();
	}

	/** UDDI lookup */
	private void uddiLookup() throws CAClientException {
		try {
			if (verbose)
				System.out.printf("Contacting UDDI at %s%n", uddiURL);
			UDDINaming uddiNaming = new UDDINaming(uddiURL);

			if (verbose)
				System.out.printf("Looking for '%s'%n", wsName);
			wsURL = uddiNaming.lookup(wsName);

		} catch (Exception e) {
			String msg = String.format("Client failed lookup on UDDI at %s!",
					uddiURL);
			throw new CAClientException(msg, e);
		}

		if (wsURL == null) {
			String msg = String.format(
					"Service with name %s not found on UDDI at %s", wsName,
					uddiURL);
			throw new CAClientException(msg);
		}
	}

	/** Stub creation and configuration */
	private void createStub() {
		if (verbose)
			System.out.println("Creating stub ...");
		service = new CAImplService();
		port = service.getCAImplPort();

		if (wsURL != null) {
			if (verbose)
				System.out.println("Setting endpoint address ...");
			BindingProvider bindingProvider = (BindingProvider) port;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, wsURL);
		}
	}

	// remote invocation methods ----------------------------------------------
	
	@Override
	public String ping(String name) {
		return port.ping(name);
	}

	@Override 
	public String requestCertificate(String name) {
			return port.requestCertificate(name);
	}


	//nao faz mal termos um método que nao esta no serviço porque é implementation first
	public Certificate GetCertificate(String name) throws Exception{
	

		try{
			/* PARA CONVERTER DE STRING PARA CERTIFICADO! */
			String certificate = this.requestCertificate(name);
			byte[] certificatebytes = parseBase64Binary(certificate);

			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			InputStream in = new ByteArrayInputStream(certificatebytes);
			X509Certificate cert = (X509Certificate)cf.generateCertificate(in);
			return cert;

		} catch ( CertificateException e ) {
			System.err.println("There was a problem converting Certificate_string to CertificateX509: " + e.getMessage() );
			return null;
		}

	
	}
}
