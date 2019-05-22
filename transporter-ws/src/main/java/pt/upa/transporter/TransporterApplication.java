package pt.upa.transporter;

import pt.upa.handler.JobHandler;
import pt.upa.transporter.ws.TransporterPort;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.ws.Endpoint;


import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;


public class TransporterApplication {
	
	private static TransporterPort _transporter;
	private static int _id;
	private static Timer _timerSimulate = new Timer();
	//FIXME private static CAClient _ca;
	
	public static void main(String[] args) throws Exception {
		//CA Address
		//_ca = new CAClient("http://localhost:8980/ca-ws/endpoint");
		//System.out.println(TransporterApplication.class.getSimpleName() + " starting...");
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", TransporterApplication.class.getName());
			return;
		}
		Timer timerProcess = new Timer();
		String uddiURL = args[0];
		String name = args[1];
		String url = args[2];
		_id = Integer.parseInt(name.replace("UpaTransporter",""));
		
		Endpoint endpoint = null;
		UDDINaming uddiNaming = null;
		try {
			_transporter = new TransporterPort(name);
			endpoint = Endpoint.create(_transporter);
			
			// publish endpoint
			System.out.printf("Starting %s%n", url);
			endpoint.publish(url);

			// publish to UDDI
			System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);
			uddiNaming = new UDDINaming(uddiURL);
			uddiNaming.rebind(name, url);

			// wait
			Random random = new Random();
			int r = (random.nextInt(5)*1000)+1;
			
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");
			TransporterApplication app = new TransporterApplication();
			timerProcess.schedule(new ProcessTransport(), 0, 5000);
			_timerSimulate.schedule(app.SimulateTransport(),100,r);
			
			System.in.read();

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
			try {
				if (uddiNaming != null) {
					// delete from UDDI
					uddiNaming.unbind(name);
					System.out.printf("Deleted '%s' from UDDI%n", name);
					
				}
			} catch (Exception e) {
				System.out.printf("Caught exception when deleting: %s%n", e);
			}
		}
		timerProcess.cancel();
		timerProcess.purge();
		_timerSimulate.cancel();
		_timerSimulate.purge();
		System.out.println("Transporter terminated");
	}
	
	private TimerTask SimulateTransport() {
		// TODO Auto-generated method stub
		return new SimulateTransport();
	}

	private static class ProcessTransport extends TimerTask {
		public void run(){
			_transporter.getHandler().processJobs(_id);
		}
	}
	
	protected class SimulateTransport extends TimerTask {
		Integer _time;
		
		SimulateTransport(){
			Random random = new Random();
			_time = (random.nextInt(5)*1000)+1;
		}
		
		public void run(){
			_transporter.getHandler().simulateJobs();
			
			//Timer auxTimer = _timerSimulate;
			//_timerSimulate = new Timer();
			//_timerSimulate.schedule(new SimulateTransport(), 0, _time);
			//auxTimer.cancel();
			//auxTimer.purge();
			this.cancel();
			_timerSimulate.purge();
			_timerSimulate.schedule(new SimulateTransport(), 100,_time);
		}
	}
}
