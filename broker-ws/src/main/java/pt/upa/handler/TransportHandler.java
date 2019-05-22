package pt.upa.handler;

import java.time.ZonedDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.InvalidPriceFault;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportStateView;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault;
import pt.upa.broker.ws.UnknownTransportFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

public class TransportHandler {

	BrokerPortType _port;
	//Transportes requeridos
	AbstractMap<String,TransportView> _currentTransports;
	
	//Transportadoras
	AbstractMap<String,TransporterClient> _transporterClients;
	
	//Relaciona TransportViews com JobViews
	AbstractMap<String, String> _transportjobViews;
	
	static List<String> _Norte =  Arrays.asList("Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança");
	static List<String> _Sul = Arrays.asList("Setúbal","Évora","Portalegre","Beja","Faro");
	static List<String> _Centro = Arrays.asList("Lisboa", "Leiria","Santarém", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda");
			
	public TransportHandler(BrokerPortType port){
		_port = port;
		_currentTransports = new HashMap<String,TransportView>();
		_transportjobViews = new HashMap<String,String>();
	}
	
	public TransportHandler(){
		_currentTransports = new HashMap<String,TransportView>();
		_transportjobViews = new HashMap<String,String>();
	}
	
	public  TransportView requestTransport(String origin, String destination, int price) throws InvalidPriceFault_Exception, UnknownLocationFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception{
		if(_port!=null){
			System.out.println("Backup done");
			_port.requestTransport(origin, destination, price);
		}
		//zonaTransporte = NC(nortecentro) ou CS(centrosul)
		String zonaTransporte = "";
		
		if(price<0){
			InvalidPriceFault f = new InvalidPriceFault();
			f.setPrice(price);
			throw new InvalidPriceFault_Exception("Price is not valid", f);
		}else if(price>100){
			UnavailableTransportFault fault = new UnavailableTransportFault();
			throw new UnavailableTransportFault_Exception("Transport with this price could not be found",fault);
		}
		
		if(!(_Sul.contains(origin)||_Centro.contains(origin)||_Norte.contains(origin))){
			UnknownLocationFault f = new UnknownLocationFault();
			f.setLocation(origin);
			throw new UnknownLocationFault_Exception("Location is not valid",f);
		}
		
		if(!(_Sul.contains(destination)||_Centro.contains(destination)||_Norte.contains(destination))){
			UnknownLocationFault f = new UnknownLocationFault();
			f.setLocation(destination);
			throw new UnknownLocationFault_Exception("Location is not valid",f);
		}
		
		if((_Centro.contains(origin)||_Sul.contains(origin))
				&&(_Centro.contains(destination)||_Sul.contains(destination))){
			//CENTRO SUL
			zonaTransporte = "CS";
		}
		
		else if((_Centro.contains(origin)||_Norte.contains(origin))
				&&(_Centro.contains(destination)||_Norte.contains(destination))){
			//NORTE CENTRO
			zonaTransporte = "NC";
		}
		
		else{
			UnavailableTransportFault f = new UnavailableTransportFault();
			f.setOrigin(origin);
			f.setDestination(destination);
			throw new UnavailableTransportFault_Exception("Transport could not be found",f);
		}
		
		ZonedDateTime date = ZonedDateTime.now();
		
		
		/*id is based on first two letters of origin and destination and also epoch seconds of the current time*/
		String id = zonaTransporte + origin.substring(0,3).toUpperCase()+destination.substring(0, 3).toUpperCase()+date.toEpochSecond();		
		
		TransportView newTransport = new TransportView();
		newTransport.setOrigin(origin);
		newTransport.setDestination(destination);
		newTransport.setPrice(price);
		newTransport.setState(TransportStateView.fromValue("REQUESTED"));
		newTransport.setId(id);
		_currentTransports.put(id, newTransport);
		
		return newTransport;
	}
	
	
	public TransportView viewTransport(String idTransView) throws UnknownTransportFault_Exception{
		TransportView t = _currentTransports.get(idTransView);
		
		if(t == null){
			UnknownTransportFault noTransport = new UnknownTransportFault();
			noTransport.setId(idTransView);
			throw new UnknownTransportFault_Exception("Unknown Transport id", noTransport);
		}
		
		if(t.getState().equals(TransportStateView.fromValue("BOOKED"))){
			//1,2,3...
			AbstractMap<JobStateView,TransportStateView> convertState = new HashMap<JobStateView,TransportStateView>();
			convertState.put(JobStateView.HEADING,TransportStateView.HEADING);
			convertState.put(JobStateView.ONGOING, TransportStateView.ONGOING);
			convertState.put(JobStateView.COMPLETED, TransportStateView.COMPLETED);
			
			//Atualiza o estado da view na transportview
			String idJobView = _transportjobViews.get(t.getId());
			String transporterCompanyName = t.getTransporterCompany();
			TransporterClient tc = _transporterClients.get(transporterCompanyName);
			JobView js = tc.jobStatus(idJobView);
			JobStateView jsv = js.getJobState();
			TransportStateView tsv = convertState.get(jsv);
			t.setState(tsv);
		}
		return t;
	}


	public List<TransportView> listTransports(){
		return new ArrayList<TransportView>(_currentTransports.values());
	}
	
	public void clearTransports(){
		_currentTransports.clear();
	}
	
	
	public List<String> listTransportsByState(String state) throws UnknownTransportFault_Exception{
		List<String> requestedList = new ArrayList<String>();
		TransportView tv;
		TransportStateView  sv;
		
		for(String key:_currentTransports.keySet()){
			tv = viewTransport(key);
			sv = tv.getState();
			
			if(state.equals("REQUESTED")){
				if(sv.equals(TransportStateView.REQUESTED)){
					//POSSIVEL ERRO
					requestedList.add(tv.getId());
				}
			}
			else if(state.equals("BUDGETED")){
				if(sv.equals(TransportStateView.BUDGETED)){
					//POSSIVEL ERRO
					requestedList.add(tv.getId());
				}
			}
		}
		return requestedList;
	}
	
	public void setConnections(AbstractMap<String,TransporterClient> transporterClients){
		_transporterClients = transporterClients;
	}
	
	public void addTransportJobViews(String transportID,String jobID){
		_transportjobViews.put(transportID, jobID);
	}
	
	public String getJobViewIDfromTransportID(String transportID){
		return _transportjobViews.get(transportID);
	}

	public void setStateTransport(String id, TransportStateView state) {
		TransportView t = _currentTransports.get(id);
		t.setState(state);
	}
}
