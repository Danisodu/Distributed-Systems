
package example.ws.handler;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.Set;

import javax.xml.soap.SOAPBody;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import java.io.InputStream;


import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;



import pt.upa.ca.ws.cli.CAClient;

/**
 *  This SOAPHandler shows how to set/get values from headers in
 *  inbound/outbound SOAP messages.
 *
 *  A header is created in an outbound message and is read on an
 *  inbound message.
 *
 *  The value that is read from the header
 *  is placed in a SOAP message context property
 *  that can be accessed by other handlers or by the application.
 */
public class SignatureHandler implements SOAPHandler<SOAPMessageContext> {

    public CAClient caclient;
    public static String whoAmI = null;
    private static String KEY_PASSWORD = "ins3cur3";
	private static String KEY_ALIAS = "keypair";
	private static String KEYSTORE_PASSWORD = "1nsecure";
    public static final String CONTEXT_PROPERTY = "my.property";


    //
    // Handler interface methods
    //

    public Set<QName> getHeaders() {
        return null;
    }

    public void setWhoAmI(String name){
        whoAmI = name; 
    }

    public boolean handleMessage(SOAPMessageContext smc) {
        System.out.println("AddHeaderHandler: Handling message.");

        Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        try {
            if (outboundElement.booleanValue()) {

        //assinar a mensagem ??
                System.out.println("Writing header in outbound SOAP message...");

                // get SOAP envelope
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();

                // add header
                SOAPHeader sh = se.getHeader();
                SOAPBody bd = (SOAPBody) se.getBody();

                //add Header
                if (sh == null)
                    sh = se.addHeader();
            

                // add header element (name, namespace prefix, namespace)
            
                //o que é o namespace??
                Name name = se.createName("Header", "h", "http://upa");
                SOAPHeaderElement element = sh.addHeaderElement(name);

          
                //###################1 passar a soapbody para bytes##############

                byte[] soapbodymessage = SOAPMessageToByteArray(bd);

                //##################2digital signature com a soapbody->mensagem############


                PrivateKey pkwhoami = getPrivateKey(whoAmI);

                byte[] cipherdigest =makedigitalSignature(soapbodymessage,pkwhoami);

                
                //####################3add header element value --> assinatura##############
                
                String header = printBase64Binary(cipherdigest);
                element.addTextNode(header);
                Name sender = se.createName("Sender", "s", "http://demo");
                SOAPHeaderElement element1 = sh.addHeaderElement(sender);
                element1.addTextNode(whoAmI);

            } else {
                System.out.println("Reading header in inbound SOAP message...");

               //vai verificar a assinatura da mensagem ??
                // get SOAP envelope header
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();
                SOAPHeader sh = se.getHeader();
                //por body

                // check header
                if (sh == null) {
                    System.out.println("Header not found.");
                    return true;
                }

                // get first header element
                Name name = se.createName("myHeader", "d", "http://upa");
                Iterator it = sh.getChildElements(name);
                // check header element
                if (!it.hasNext()) {
                    System.out.println("Header element not found.");
                    return true;
                }
                SOAPElement element = (SOAPElement) it.next();

                // get header element value
                String valueString = element.getValue();
                int value = Integer.parseInt(valueString);

                // print received header
                System.out.println("Header value is " + value);

                // put header in a property context
                smc.put(CONTEXT_PROPERTY, value);
                // set property scope to application client/server class can access it
                smc.setScope(CONTEXT_PROPERTY, Scope.APPLICATION);

            }
        } catch (Exception e) {
            System.out.print("Caught exception in handleMessage: ");
            System.out.println(e);
            System.out.println("Continue normal processing...");
        }

        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
        System.out.println("Ignoring fault message...");
        return true;
    }

    public void close(MessageContext messageContext) {
    }

//#########################################make signature methods###############################################
     public byte[] SOAPMessageToByteArray(SOAPBody bd) throws Exception {

        String stringbd = bd.getTextContent();
                                            
        byte[] msgByteArray = parseBase64Binary(stringbd);
        return msgByteArray;
    }




    public byte[] makedigitalSignature(byte[] bytearraybody, PrivateKey pkwhoiam) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException{
    
        Signature sig= Signature.getInstance("SHA1WithRSA");
        sig.initSign(pkwhoiam);
        sig.update(bytearraybody);
        return sig.sign();
    }


     public PrivateKey getPrivateKey(String whoAmI) throws Exception{


        return getPrivateKeyFromkeystore(whoAmI,KEYSTORE_PASSWORD.toCharArray(),KEY_ALIAS, KEY_PASSWORD.toCharArray());      
    }

    public KeyStore readKeystoreFile(String whoAmI, char[] keyStorePassword)throws Exception{
        Class cls = Class.forName("SignatureHandler");
        ClassLoader cLoader = cls.getClassLoader();

        //é preciso dar o caminho e retornar o caminho

        String keystore = whoAmI + ".jks";
        InputStream file = cLoader.getResourceAsStream(keystore);

        //byte[] bcontent = new byte[file.available()];

        
        KeyStore keystore1 = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore1.load(file, keyStorePassword);
        
        return keystore1;

    }

    public PrivateKey getPrivateKeyFromkeystore(String whoAmI, char[] KeyStorePassword, String KeyAlias, char[] KeyPassword) throws Exception {
        
        KeyStore keystore = readKeystoreFile(whoAmI, KeyStorePassword);
        PrivateKey pkey= (PrivateKey) keystore.getKey(KeyAlias, KeyPassword);

        return pkey;

    }

//#########################################verify signature methods###############################################
   

    public Certificate GetCertificate(String companyName) throws Exception{
        return caclient.GetCertificate(companyName);
    }
    
    public boolean verifySignedCertificate(Certificate certificate, PublicKey caPublicKey) {
        try {
            certificate.verify(caPublicKey);
        } catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
                | SignatureException e) {

            return false;
        }
        return true;
    }



}