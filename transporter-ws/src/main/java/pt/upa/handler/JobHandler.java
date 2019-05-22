package pt.upa.handler;

import java.time.ZonedDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import pt.upa.transporter.ws.BadJobFault;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;

public class JobHandler {
	
	static List<String> _Norte =  Arrays.asList("Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança");
	static List<String> _Sul = Arrays.asList("Setúbal","Évora","Portalegre","Beja","Faro");
	
	static List<String> _Centro = Arrays.asList("Lisboa", "Leiria","Santarém", "Castelo Branco", "Coimbra", "Aveiro",
			"Viseu", "Guarda");
	
	AbstractMap<String,JobView> _currentJobs;
	String _companyName;
	
	public JobHandler(String compName){
		_currentJobs = new HashMap<String,JobView>();
		_companyName = compName;
	}
	
	
	public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception,BadPriceFault_Exception{
		
		if((_Norte.contains(origin)&&_Sul.contains(destination))
				||((_Sul.contains(origin)&&_Norte.contains(destination)))){
			BadLocationFault fault = new BadLocationFault();
			fault.setLocation(origin);
			throw new BadLocationFault_Exception("Invalid origin/destination location",fault);
		}else if(!(_Norte.contains(origin))&&!(_Sul.contains(origin))&&!(_Centro.contains(origin))){
			BadLocationFault fault = new BadLocationFault();
			fault.setLocation(origin);
			throw new BadLocationFault_Exception("Invalid origin location",fault);
		}else if(!(_Norte.contains(destination))&&!(_Sul.contains(destination))&&!(_Centro.contains(destination))){
			BadLocationFault fault = new BadLocationFault();
			fault.setLocation(destination);
			throw new BadLocationFault_Exception("Invalid destination location",fault);
		}
		
		if(price < 0){ 
			BadPriceFault badPrice = new BadPriceFault();
			badPrice.setPrice(price);			
			throw new BadPriceFault_Exception("Price not valid", badPrice);
		}else if(price > 100)
			return null;
		
		ZonedDateTime date = ZonedDateTime.now();
		
		/*id is based on first two letters of origin and destination and also epoch seconds of the current time*/
		String id = origin.substring(0,3).toUpperCase()+destination.substring(0, 3).toUpperCase()+date.toEpochSecond();
		
		JobView job = new JobView();
		job.setJobOrigin(origin);
		job.setJobDestination(destination);
		job.setJobPrice(price);	
		job.setJobIdentifier(id);
		job.setJobState(JobStateView.fromValue("PROPOSED"));
		job.setCompanyName(_companyName);

		_currentJobs.put(id, job);
		int compID = Character.getNumericValue(_companyName.charAt(14));
		processJobs(compID);
		
		return job;
	}
	
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		
		if(!(_currentJobs.containsKey(id))||
				!_currentJobs.get(id).getJobState().equals(JobStateView.PROPOSED)){
			BadJobFault fault = new BadJobFault();
			fault.setId(id);
			throw new BadJobFault_Exception("Job not found",fault);
		}
		JobView job =_currentJobs.get(id);
		
		if(accept)
			job.setJobState(JobStateView.fromValue("ACCEPTED"));
		else
			job.setJobState(JobStateView.fromValue("REJECTED"));
		return job;
	}
	
	public JobView jobStatus(String id){

		return _currentJobs.get(id);
	}
	
	public List<JobView> listJobs(){
		return new ArrayList<JobView>(_currentJobs.values());
	}
	
	public void clearJobs(){
		_currentJobs.clear();		
	}

	public void processJobs(int transporterId) {
		boolean odd =  transporterId%2 != 0;
		Random r = new Random(); 
		int price = 0;
		
		for(String key : _currentJobs.keySet()){
			JobView job = _currentJobs.get(key);
			if(!job.getJobState().equals(JobStateView.PROPOSED))
				continue;
			int requestedPrice = job.getJobPrice();
			if(requestedPrice<=10){
				price = r.nextInt(9) + 1;
			
			}else if(odd){
				if(requestedPrice%2 != 0){
					price = r.nextInt(job.getJobPrice() -1);
				}
				else{
					price = r.nextInt(99 + job.getJobPrice()) + job.getJobPrice() + 1;
				}
				
			}else{
				if(requestedPrice%2 == 0){
					price = r.nextInt(job.getJobPrice() -1 );
				}
				else{
					price = r.nextInt(99 + job.getJobPrice()) + job.getJobPrice() + 1;
				}
			}
			job.setJobPrice(price);
		}
	}
	
	public void simulateJobs(){
		AbstractMap<JobStateView,JobStateView> nextState = new HashMap<JobStateView,JobStateView>();
		nextState.put(JobStateView.ACCEPTED,JobStateView.HEADING);
		nextState.put(JobStateView.HEADING,JobStateView.ONGOING);
		nextState.put(JobStateView.ONGOING, JobStateView.COMPLETED);
		nextState.put(JobStateView.COMPLETED, JobStateView.COMPLETED);
		nextState.put(JobStateView.REJECTED, JobStateView.REJECTED);
		nextState.put(JobStateView.PROPOSED, JobStateView.PROPOSED);
		
		for(JobView job : this.listJobs()){
			JobStateView currentState = job.getJobState();
			job.setJobState(nextState.get(currentState));
		}
	}
	
}
