package pt.upa.broker.ws;
import java.util.HashMap;
import java.util.List;

import javax.jws.WebService;

import pt.upa.handler.TransportHandler;

@WebService(
    endpointInterface="pt.upa.broker.ws.BrokerPortType",
    wsdlLocation="broker.2_0.wsdl",
    name="Broker",
    portName="BrokerPort",
    targetNamespace="http://ws.broker.upa.pt/",
    serviceName="BrokerService"
)
public class BrokerPort implements BrokerPortType{
	TransportHandler _handler;
	
	public String ping(String name){
		return "Broker: " + name + " ping\n";
	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
			TransportView t = _handler.requestTransport(origin, destination, price);
		
		return t.getId();
	}

	@Override
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		
		return _handler.viewTransport(id);
	}

	@Override
	public List<TransportView> listTransports() {
		
		return _handler.listTransports();
	}

	@Override
	public void clearTransports() {
		
		_handler.clearTransports();
	}
	
	@Override 
	public void setStateTransport(String id, TransportStateView state) {
		System.out.println("Backup done");
		_handler.setStateTransport(id, state);		
	}
	
	public TransportHandler getHandler(){
		return _handler;
	}
	
	public void setBackup(BrokerPortType _backup){
		if(_handler == null){
			if(_backup==null)
				_handler = new TransportHandler();
			else
				_handler = new TransportHandler(_backup);
		}
	}
	
	
}
