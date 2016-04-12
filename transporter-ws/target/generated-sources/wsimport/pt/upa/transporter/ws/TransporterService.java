
package pt.upa.transporter.ws;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.10
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "TransporterService", targetNamespace = "http://ws.transporter.upa.pt/", wsdlLocation = "file:/Users/claudiaamorim/Desktop/SD/Projeto/A_53-project/transporter-ws/src/main/resources/transporter.1_0.wsdl")
public class TransporterService
    extends Service
{

    private final static URL TRANSPORTERSERVICE_WSDL_LOCATION;
    private final static WebServiceException TRANSPORTERSERVICE_EXCEPTION;
    private final static QName TRANSPORTERSERVICE_QNAME = new QName("http://ws.transporter.upa.pt/", "TransporterService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("file:/Users/claudiaamorim/Desktop/SD/Projeto/A_53-project/transporter-ws/src/main/resources/transporter.1_0.wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        TRANSPORTERSERVICE_WSDL_LOCATION = url;
        TRANSPORTERSERVICE_EXCEPTION = e;
    }

    public TransporterService() {
        super(__getWsdlLocation(), TRANSPORTERSERVICE_QNAME);
    }

    public TransporterService(WebServiceFeature... features) {
        super(__getWsdlLocation(), TRANSPORTERSERVICE_QNAME, features);
    }

    public TransporterService(URL wsdlLocation) {
        super(wsdlLocation, TRANSPORTERSERVICE_QNAME);
    }

    public TransporterService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, TRANSPORTERSERVICE_QNAME, features);
    }

    public TransporterService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public TransporterService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns TransporterPortType
     */
    @WebEndpoint(name = "TransporterPort")
    public TransporterPortType getTransporterPort() {
        return super.getPort(new QName("http://ws.transporter.upa.pt/", "TransporterPort"), TransporterPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns TransporterPortType
     */
    @WebEndpoint(name = "TransporterPort")
    public TransporterPortType getTransporterPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://ws.transporter.upa.pt/", "TransporterPort"), TransporterPortType.class, features);
    }

    private static URL __getWsdlLocation() {
        if (TRANSPORTERSERVICE_EXCEPTION!= null) {
            throw TRANSPORTERSERVICE_EXCEPTION;
        }
        return TRANSPORTERSERVICE_WSDL_LOCATION;
    }

}
