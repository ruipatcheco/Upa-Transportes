package pt.upa.broker.ws.cli;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

public class BrokerClient {

	// TODO
	
	BrokerPortType _port;
	List<String> _idList;
	
	public BrokerClient(String endpointAddress) throws Exception{
		this.connect(endpointAddress);
		_idList = new ArrayList<String>();
	}
	
	private void  connect(String endpointAddress) throws Exception{
		if (endpointAddress == null) {
			System.out.println("Not found!");
			return;
		} else {
			System.out.printf("Found %s%n", endpointAddress);
		}
		
		String url = endpointAddress;

		System.out.println("Creating stub ...");
		
		BrokerService service = new BrokerService();
		_port = service.getBrokerPort();
		
		System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider = (BindingProvider) _port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, url);
        System.out.printf("Remote call to %s ...%n", url);

        int connectionTimeout = 1000;
        // The connection timeout property has different names in different versions of JAX-WS
        // Set them all to avoid compatibility issues
        final List<String> CONN_TIME_PROPS = new ArrayList<String>();
        CONN_TIME_PROPS.add("com.sun.xml.ws.connect.timeout");
        CONN_TIME_PROPS.add("com.sun.xml.internal.ws.connect.timeout");
        CONN_TIME_PROPS.add("javax.xml.ws.client.connectionTimeout");
        // Set timeout until a connection is established (unit is milliseconds; 0 means infinite)
        for (String propName : CONN_TIME_PROPS)
            requestContext.put(propName, connectionTimeout);
        System.out.printf("Set connection timeout to %d milliseconds%n", connectionTimeout);

        int receiveTimeout = 2000;
        // The receive timeout property has alternative names
        // Again, set them all to avoid compability issues
        final List<String> RECV_TIME_PROPS = new ArrayList<String>();
        RECV_TIME_PROPS.add("com.sun.xml.ws.request.timeout");
        RECV_TIME_PROPS.add("com.sun.xml.internal.ws.request.timeout");
        RECV_TIME_PROPS.add("javax.xml.ws.client.receiveTimeout");
        // Set timeout until the response is received (unit is milliseconds; 0 means infinite)
        for (String propName : RECV_TIME_PROPS)
            requestContext.put(propName, 1000);
        System.out.printf("Set receive timeout to %d milliseconds%n", receiveTimeout);
	}
	
	public String ping(String name){
		return _port.ping(name);
	}
		
	public String requestTransport(String origin,String destination,int price) throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception{
		String id = _port.requestTransport(origin, destination, price);
		_idList.add(id);
		return id;
	}
	
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception{
		return _port.viewTransport(id);
	}
	
	public List<TransportView> listTransports(){
		return _port.listTransports();
	}
	
	public List<String> getIdList(){
		return _idList;
	}
}
