package pt.upa.ca.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.ca.ws.CAPortType;
import pt.upa.ca.ws.CAService;
import pt.upa.ca.ws.Certificate;

public class CAClient implements CAPortType {
	/** WS service */
	CAService service = null;

	/** WS port (port type is the interface, port is the implementation) */
	CAPortType port = null;

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
	private boolean verbose = false;

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
		service = new CAService();
		port = service.getCAPort();

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
	
	public PublicKey getPublicKey(String name) throws Exception {
		return null;
//		return port.getPublicKey(name);
	}
	
	public byte[] makeDigitalSignature(Certificate certificate, PrivateKey privatekey) throws Exception{
		return null;
//		return port.makeDigitalSignature(certificate,privatekey);
	}	

}
