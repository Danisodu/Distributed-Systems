package pt.upa.ca.ws;

import java.security.PublicKey;
import org.joda.time.DateTime;

public class Certificate{
	
 	private String companyName;
//    private PublicKey pubkey;
    private DateTime timeout;

    public Certificate(){}
    
    public Certificate(String companyName, DateTime timeout) {
    	setCompanyName(companyName);
    	//setPubKey(pubkey);
    	setTimeout(timeout);
	}

	public String getCompanyName() {
		return companyName;
	}
	
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	/*public PublicKey getPubKey() {
		return pubkey;
	}*/

	/*public void setPubKey(PublicKey pubkey) {
		this.pubkey=pubkey;
	}*/
	
	public DateTime getTimeout() {
		return timeout;
	}
	
	public void setTimeout(DateTime timeout) {
		this.timeout = timeout;
	}
	
}