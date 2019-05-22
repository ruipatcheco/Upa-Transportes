package pt.upa.transporter.ws.cli;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.handler.WSHandler;
import pt.upa.transporter.ws.*;

import java.util.Map;

import javax.jws.HandlerChain;
import javax.xml.ws.BindingProvider;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

@HandlerChain(file = "/transporterWS_CLI_handler-chain.xml")
public class TransporterClient {
	String _company;
	TransporterPortType port;
	String _endpointAddress;
	
	public static final String KEYSTORE = "/Client.jks";
	public static final String KSPASSWORD = "ins3cur3";
	public static final String KEYALIAS = "UpaBroker";
	public static final String KEYPASSWORD = "1nsecure";
	
	
	public TransporterClient(String endpointAddress) throws Exception{
		_endpointAddress = endpointAddress;
		_company = "UpaTransporter"+endpointID(endpointAddress);
		this.connect();
	}
	
	
	private int endpointID(String endpointAddress) {
		return Character.getNumericValue(endpointAddress.charAt(20));
	}
	
	public String getCompanyName(){
		return _company;
	}

	public void connect() throws Exception {
				if (_endpointAddress == null) {
			System.out.println("Not found!");
			return;
		} else {
			System.out.printf("Found %s%n", _endpointAddress);
		}

		System.out.println("Creating stub ...");
		
		TransporterService service = new TransporterService();
		port = service.getTransporterPort();
		
		System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		String metaData = KEYSTORE+"#"+KSPASSWORD+"#"+KEYALIAS+"#"+KEYPASSWORD;
		requestContext.put("my.request.property", metaData);
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, _endpointAddress);	
	}
	

	public String ping(String name){
		return port.ping(name);
	}
	
	public JobView jobStatus(String id){
		return port.jobStatus(id);
	}
	
	public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception{
		return port.requestJob(origin, destination, price);
	}
	
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception{
		return port.decideJob(id,accept);
	}
}
