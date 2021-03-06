
package example.ws.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
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
                Name sender = se.createName("Sender", "s", "http://sender");
                SOAPHeaderElement element1 = sh.addHeaderElement(sender);
                element1.addTextNode(whoAmI);

            } else {
                System.out.println("Reading header in inbound SOAP message...");

               //vai verificar a assinatura da mensagem 
                // get SOAP envelope header
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();
                SOAPHeader sh = se.getHeader();
                SOAPBody bd = (SOAPBody) se.getBody();

                //por body

                // check header
                if (sh == null) {
                    System.out.println("Header not found.");
                    return true;
                }

                

                // get header element value
                //String valueString = element.getValue();
                //int value = Integer.parseInt(valueString);

                //##############1 obter assinatura recebida (em bytes)#######
                
                // get first header element
                Name name = se.createName("Header", "h", "http://upa");
                Iterator it = sh.getChildElements(name);
                // check header element
                if (!it.hasNext()) {
                    System.out.println("Header element not found.");
                    return true;
                }
                SOAPElement element = (SOAPElement) it.next();
                String valueString = element.getValue();

                byte[] signature = parseBase64Binary(valueString);


                //#####################2 obter whoAmI#########################
                Name name1 = se.createName("Sender", "s", "http://sender");
                Iterator it1 = sh.getChildElements(name1);
                // check header element
                if (!it1.hasNext()) {
                    System.out.println("Sender element not found.");
                    return true;
                }
                SOAPElement element1 = (SOAPElement) it1.next();

                whoAmI = element1.getValue();

                //#####################3 obter o soapBody#####################

                byte[] soapbodymessage = SOAPMessageToByteArray(bd);

                //#####################Digest Verify########################

                    //########obter chave publica do whoAmI########

                PublicKey pubkey = getPublicKeyFromSender();
               
                DigestVerify(soapbodymessage, signature, pubkey);



                // put header in a property context
                smc.put(CONTEXT_PROPERTY, signature);
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

        String keystore = whoAmI + ".jks";
        InputStream file = cLoader.getResourceAsStream(keystore);

        
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
        Certificate cer = caclient.GetCertificate(companyName);
        PublicKey pubkeyca = getPublicKeyCA();
        verifySignedCertificate(cer,pubkeyca);
        return cer;
        
        
    }

    public PublicKey getPublicKeyCA() throws ClassNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException{
        Class cls = Class.forName("SignatureHandler");
        ClassLoader cLoader = cls.getClassLoader();

        String keystore = "ca-key.pem";
        InputStream file = cLoader.getResourceAsStream(keystore);

        byte[] content = new byte[file.available()];
        file.read(content);
        file.close();

        X509EncodedKeySpec pubspec = new X509EncodedKeySpec(content);
        KeyFactory keyfacPub = KeyFactory.getInstance("RSA");

        return keyfacPub.generatePublic(pubspec); 

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

    public PublicKey getPublicKeyFromCertificate(Certificate certificate) {
        return certificate.getPublicKey();
    }

    public PublicKey getPublicKeyFromSender() throws Exception{

        Certificate cer = GetCertificate(whoAmI);
        return getPublicKeyFromCertificate(cer);


    }


    public boolean DigestVerify(byte[] soapbody, byte[] signature, PublicKey pubkeysender)throws Exception{
        Signature sig = Signature.getInstance("SHA1WithRSA");
        sig.initVerify(pubkeysender);
        sig.update(soapbody);
        try{
            return sig.verify(signature);
        } catch(SignatureException se){
            System.err.println("Caught exception while verifying " + se);
            return false;
        }
    }


}