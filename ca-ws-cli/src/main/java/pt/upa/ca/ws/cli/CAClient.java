package pt.upa.ca.ws.cli;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.ca.ws.Authority;
import pt.upa.ca.ws.AuthorityImplService;
import pt.upa.ca.ws.UnavaliableCertificateException_Exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jws.HandlerChain;
import javax.xml.ws.BindingProvider;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

//@HandlerChain(file = "/ca_CLI_handler-chain.xml")
public class CAClient {

	// TODO
	
	Authority _port;
	List<String> _idList;
	
	public CAClient(String endpointAddress){
		this.connect(endpointAddress);
		_idList = new ArrayList<String>();
	}
	
	private void  connect(String endpointAddress){
		if (endpointAddress == null) {
			System.out.println("Not found!");
			return;
		} else {
			System.out.printf("Found %s%n", endpointAddress);
		}

		System.out.println("Creating stub ...");
		
		AuthorityImplService service = new AuthorityImplService();
		_port = service.getAuthorityImplPort();
		
		System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider = (BindingProvider) _port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
	}
	
	public String getCertificate(String name) throws UnavaliableCertificateException_Exception{
		return _port.getCertificate(name);
	}
}
