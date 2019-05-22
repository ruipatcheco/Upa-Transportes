package pt.upa.transporter.ws;

import java.util.List;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import pt.upa.handler.JobHandler;
//import pt.upa.handler.WSHandler;

@WebService(
    endpointInterface="pt.upa.transporter.ws.TransporterPortType",
    wsdlLocation="transporter.1_0.wsdl",
    name="Transporter",
    portName="TransporterPort",
    targetNamespace="http://ws.transporter.upa.pt/",
    serviceName="TransporterService"
)

@HandlerChain(file = "/transporterWS_handler-chain.xml")
public class TransporterPort implements TransporterPortType{
	
	JobHandler _handler;
	
	public String KEYSTORE = "/UpaTransporter.jks";
	public String KSPASSWORD = "ins3cur3";
	public String KEYALIAS;
	public String KEYPASSWORD = "1nsecure";
	public String _metaData;
	
	@Resource
	private WebServiceContext webServiceContext;
	
	public TransporterPort(String companyName){
		_handler = new JobHandler(companyName);
		KEYALIAS = companyName;
		_metaData=KEYSTORE+"#"+KSPASSWORD+"#"+KEYALIAS+"#"+KEYPASSWORD;		
	}

	
	public String ping(String name){
		SignMessage();
		return "Transporter: " + name + " ping\n";
	}

	@Override
	public JobView requestJob(String origin, String destination, int price)
			throws BadLocationFault_Exception, BadPriceFault_Exception {
		SignMessage();
			
		return _handler.requestJob(origin, destination, price);
	}

	@Override
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		SignMessage();
		
		return _handler.decideJob(id, accept);
	}

	@Override
	public JobView jobStatus(String id) {
		SignMessage();
		
		return _handler.jobStatus(id);
	}

	@Override
	public List<JobView> listJobs() {
		SignMessage();
		
		return _handler.listJobs();
	}

	@Override
	public void clearJobs() {
		SignMessage();
		
		_handler.clearJobs();
	}
	
	public JobHandler getHandler(){
		return _handler;
	}
	
	private void SignMessage(){
		MessageContext context = webServiceContext.getMessageContext();
		context.put("my.request.property", _metaData);
	}

}
