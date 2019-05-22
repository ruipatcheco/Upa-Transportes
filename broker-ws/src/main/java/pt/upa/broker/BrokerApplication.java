package pt.upa.broker;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.net.ConnectException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.jws.HandlerChain;
import javax.xml.ws.BindingProvider;
import javax.xml.bind.DatatypeConverter;
import javax.xml.registry.JAXRException;
import javax.xml.ws.Endpoint;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.BrokerApplication;
import pt.upa.broker.exceptions.UntrustedMessageException;
import pt.upa.broker.ws.BrokerPort;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.InvalidPriceFault;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportStateView;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;
//FIXME import pt.upa.ca.ws.cli.CAClient;
import pt.upa.handler.TransportHandler;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.TransporterService;
import pt.upa.transporter.ws.cli.TransporterClient;

public class BrokerApplication {
	
	static AbstractMap<String,TransporterClient> _transporterClients;
	static AbstractMap<String, String> _transportjobViews;
	static AbstractMap<String,Certificate> _certificateList;
	static BrokerPort _broker;
	static BrokerPortType _backup;
	static BrokerPortType _primary;
	static UDDINaming uddiNaming = null;
	static String uddiURL;
	static String url;
	static Timer processTimer;
	
	public static void main(String[] args) throws Exception {
		//CA Address
		System.out.println(BrokerApplication.class.getSimpleName() + " starting...");
	
		url = args[2];
		String name = args[1];
		uddiURL = args[0];
		
		Endpoint endpoint = null;
		//UDDINaming uddiNaming = null;
		
		if(name.equals("UpaBroker0")){
		
			try {
				_broker = new BrokerPort();
				endpoint = Endpoint.create(_broker);
	
				// publish endpoint
				System.out.printf("Starting %s%n", url);
				endpoint.publish(url);
	
				// wait
				System.out.println("Awaiting connections");
				System.out.println("Press enter to shutdown");
				uddiNaming = new UDDINaming(uddiURL);
	
				System.out.printf("Publishing UpaBroker to UDDI at %s%n", uddiURL);
				uddiNaming.rebind("UpaBroker", url);
				
				String endpointBackup = "http://localhost:8180/broker-ws/endpoint";
				
				BrokerService service = new BrokerService();
				_backup = service.getBrokerPort();
				
				
				System.out.println("Waiting 10 seconds for backup server to start ...");
				Thread.sleep(10000);
				
				System.out.println("Setting endpoint address ...");
				BindingProvider bindingProvider = (BindingProvider) _backup;
				
				try{
					Map<String, Object> requestContext = bindingProvider.getRequestContext();
					requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointBackup);
					//System.out.println("Backup server has started");
					_broker.setBackup(_backup);
				}catch(Exception e){
					System.out.println("Backup server not started");
					_broker.setBackup(null);					
				}

				ArrayList<String> _endpoints = new ArrayList<String>(uddiNaming.list("UpaTransporter%"));
				AbstractMap<String,TransporterClient> _transporterClients = new HashMap<String,TransporterClient>();
				
				for(String endpoints : _endpoints){
					TransporterClient client = new TransporterClient(endpoints);
					_transporterClients.put(client.getCompanyName(),client);
				}
				Timer processTimer =  new Timer();
				
				processTimer.schedule(new JobDecider(), 0, 1000);
				
				System.in.read();
				
				processTimer.cancel();
			
			} catch (Exception e) {
				System.out.printf("Caught exception: %s%n", e);
				e.printStackTrace();
	
			} finally {
				try {
					if (endpoint != null) {
						// stop endpoint
						endpoint.stop();
						System.out.printf("Stopped %s%n", url);
					}
				} catch (Exception e) {
					System.out.printf("Caught exception when stopping: %s%n", e);
				}
				try{
					if (uddiNaming != null) {
						// delete from UDDI
						uddiNaming.unbind(name);
						System.out.printf("Deleted '%s' from UDDI%n", name);
				}
				}catch (Exception e) {
					System.out.printf("Caught exception when deleting: %s%n", e);
				}
				
			}
		}
		else{
			try {
				_broker = new BrokerPort();
				endpoint = Endpoint.create(_broker);
				
				_broker.setBackup(null);
				// publish endpoint
				System.out.printf("Starting %s%n", url);
				endpoint.publish(url);
	
				// wait
				System.out.println("Awaiting connections");
				System.out.println("Press enter to shutdown");
				

				
				uddiNaming = new UDDINaming(uddiURL);
	
				String endpointPrimary = "http://localhost:8080/broker-ws/endpoint";
				
				BrokerService service = new BrokerService();
				_primary = service.getBrokerPort();
				
				//Thread.sleep(10000);
				
				System.out.println("Setting endpoint address ...");
				BindingProvider bindingProvider = (BindingProvider) _primary;
				Map<String, Object> requestContext = bindingProvider.getRequestContext();
				requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointPrimary);
				
				ArrayList<String> _endpoints = new ArrayList<String>(uddiNaming.list("UpaTransporter%"));
				AbstractMap<String,TransporterClient> _transporterClients = new HashMap<String,TransporterClient>();
				
				for(String endpoints : _endpoints){
					TransporterClient client = new TransporterClient(endpoints);
					_transporterClients.put(client.getCompanyName(),client);
				}
				
				processTimer =  new Timer();
				
				processTimer.schedule(new BackupServer(), 0, 1000);
				
				System.in.read();
				

				processTimer.cancel();
				processTimer.purge();
			} catch (Exception e) {
				System.out.printf("Caught exception: %s%n", e);
				e.printStackTrace();
	
			} finally {
				try {
					if (endpoint != null) {
						// stop endpoint
						endpoint.stop();
						System.out.printf("Stopped %s%n", url);
					}
				} catch (Exception e) {
					System.out.printf("Caught exception when stopping: %s%n", e);
				}
			}
			
		}

	}
	
	public static AbstractMap<String, List<JobView>> sendRequests(TransportHandler h) throws UnknownTransportFault_Exception, 
	InvalidPriceFault_Exception, UnknownLocationFault_Exception, BadLocationFault_Exception, 
	BadPriceFault_Exception, UnavailableTransportFault_Exception{
		
		String origin, destination;
		int price;
		AbstractMap<String, List<JobView>> _jobOffers = new HashMap<String,List<JobView>>();
		
		List<String> l = h.listTransportsByState("REQUESTED");
		for(String s:l){
			TransportView t = h.viewTransport(s);
			origin = t.getOrigin();
			destination = t.getDestination();
			price = t.getPrice();
			String zonaTransporte = t.getId().substring(0, 2);
			
			if(zonaTransporte == "CS"){
				//CENTROSUL
				List<JobView> aux = new ArrayList<JobView>();
				for(int i=1; i<_transporterClients.size();i+=2){
					JobView j = _transporterClients.get(i-1).requestJob(origin, destination, price);
					if(j!=null){
						aux.add(j);
						t.setState(TransportStateView.fromValue("BUDGETED"));
					}
					else{
						t.setState(TransportStateView.fromValue("FAILED"));
					}
				}
				//s= id do TransportView, aux= lista de JobView (contem ID do job)
				_jobOffers.put(s, aux);
			}
			
			if(zonaTransporte == "NC"){
				//NORTECENTRO
				List<JobView> aux = new ArrayList<JobView>();
				for(int i=2; i<_transporterClients.size();i+=2){
					JobView j = _transporterClients.get(i-1).requestJob(origin, destination, price);
					if(j!=null){
						aux.add(j);
						t.setState(TransportStateView.fromValue("BUDGETED"));
					}
					else{
						t.setState(TransportStateView.fromValue("FAILED"));
					}
				}
				//s= id do TransportView, aux= lista de JobView (contem ID do job)
				_jobOffers.put(s, aux);
			}
		}
		return _jobOffers;
	}
	
	public static void chooseTransporter(TransportHandler h, AbstractMap<String, List<JobView>> _jobOffers) throws UnknownTransportFault_Exception, BadJobFault_Exception{
		List<String> l = h.listTransportsByState("BUDGETED");
		
		for(String s:l){
			//s= id do TransportView, aux=lista de JobView (contem ID do job)
			TransportView t = h.viewTransport(s);
			int bestOffer = 100;
			//id do JobView
			String bestJobViewID = null;
			List<JobView> aux =_jobOffers.get(s);
			
			for(JobView j:aux){
				if(j.getJobPrice()<bestOffer){
					bestOffer = j.getJobPrice();
					bestJobViewID = j.getJobIdentifier();
				}
			}
			
			if(bestJobViewID==null){
				//Nao ha ofertas
				t.setState(TransportStateView.fromValue("FAILED"));
				if(_backup!=null)
					_backup.setStateTransport(t.getId(),TransportStateView.fromValue("FAILED"));
				return;
			}
			
			for(JobView j:aux){
				String companyID = j.getCompanyName();
				if(j.getJobIdentifier().equals(bestJobViewID)){
					_transporterClients.get(companyID).decideJob(bestJobViewID, true);
					t.setState(TransportStateView.fromValue("BOOKED"));
					t.setTransporterCompany(j.getCompanyName());
					h.addTransportJobViews(t.getId(), bestJobViewID);
					_backup.setStateTransport(t.getId(),TransportStateView.fromValue("BOOKED"));
				}
				else{
					_transporterClients.get(companyID).decideJob(bestJobViewID, false);
					t.setState(TransportStateView.fromValue("FAILED"));
					_backup.setStateTransport(t.getId(),TransportStateView.fromValue("FAILED"));
				}
			}
		}
	}
	
	private static class JobDecider extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			TransportHandler h = _broker.getHandler();
			System.out.println("JobDecider");
			h.setConnections(_transporterClients);
			
			AbstractMap<String, List<JobView>> _jobOffers;
			try {
				_jobOffers = sendRequests(h);
				//Relaciona TransportViews com JobViews
				chooseTransporter(h,_jobOffers);
			} catch (UnknownTransportFault_Exception | InvalidPriceFault_Exception | UnknownLocationFault_Exception
					| BadLocationFault_Exception | BadPriceFault_Exception | UnavailableTransportFault_Exception| BadJobFault_Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		
		}
	}
	
	private static class BackupServer extends TimerTask{
		@Override
		public void run(){
			try{
				_primary.ping("teste");
				//_broker.setHandler(h);
			}catch (javax.xml.ws.WebServiceException | NullPointerException e){
				this.cancel();
				System.out.printf("Publishing UpaBroker to UDDI at %s%n", uddiURL);
				try {
					uddiNaming = new UDDINaming(uddiURL);
					uddiNaming.rebind("UpaBroker", url);
				} catch (JAXRException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("Main Server is offline...");
				processTimer.purge();
				processTimer.schedule(new JobDecider(), 0, 1000);
				
			}
			
		}
		
	}
}
