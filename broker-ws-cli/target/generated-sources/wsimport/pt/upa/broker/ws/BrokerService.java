
package pt.upa.broker.ws;

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
@WebServiceClient(name = "BrokerService", targetNamespace = "http://ws.broker.upa.pt/", wsdlLocation = "file:/Volumes/SamsungSD/MEGA/IST/3\u00baano/2\u00baSemestre/SD/Projeto/broker-ws-cli/../broker-ws/src/main/resources/broker.2_0.wsdl")
public class BrokerService
    extends Service
{

    private final static URL BROKERSERVICE_WSDL_LOCATION;
    private final static WebServiceException BROKERSERVICE_EXCEPTION;
    private final static QName BROKERSERVICE_QNAME = new QName("http://ws.broker.upa.pt/", "BrokerService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("file:/Volumes/SamsungSD/MEGA/IST/3\u00baano/2\u00baSemestre/SD/Projeto/broker-ws-cli/../broker-ws/src/main/resources/broker.2_0.wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        BROKERSERVICE_WSDL_LOCATION = url;
        BROKERSERVICE_EXCEPTION = e;
    }

    public BrokerService() {
        super(__getWsdlLocation(), BROKERSERVICE_QNAME);
    }

    public BrokerService(WebServiceFeature... features) {
        super(__getWsdlLocation(), BROKERSERVICE_QNAME, features);
    }

    public BrokerService(URL wsdlLocation) {
        super(wsdlLocation, BROKERSERVICE_QNAME);
    }

    public BrokerService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, BROKERSERVICE_QNAME, features);
    }

    public BrokerService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public BrokerService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns BrokerPortType
     */
    @WebEndpoint(name = "BrokerPort")
    public BrokerPortType getBrokerPort() {
        return super.getPort(new QName("http://ws.broker.upa.pt/", "BrokerPort"), BrokerPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns BrokerPortType
     */
    @WebEndpoint(name = "BrokerPort")
    public BrokerPortType getBrokerPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://ws.broker.upa.pt/", "BrokerPort"), BrokerPortType.class, features);
    }

    private static URL __getWsdlLocation() {
        if (BROKERSERVICE_EXCEPTION!= null) {
            throw BROKERSERVICE_EXCEPTION;
        }
        return BROKERSERVICE_WSDL_LOCATION;
    }

}
