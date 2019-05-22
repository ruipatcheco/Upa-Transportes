package pt.upa.broker;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import com.sun.xml.ws.client.ClientTransportException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.cli.BrokerClient;

public class BrokerClientApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(BrokerClientApplication.class.getSimpleName() + " starting...");

		//check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
        } else {
            // set endpoint address
            String uddiURL = args[0];
			UDDINaming uddiNaming = new UDDINaming(uddiURL);
			
            String url = uddiNaming.lookup("UpaBroker");
            
            System.out.println(url);
            
            BrokerClient b = new BrokerClient(url);            
            
            // call using set endpoint address
            try {
            	System.out.println(b.ping("Client"));
                Thread.sleep(5000);
                System.out.println(b.ping("CLI"));                

            } catch(ClientTransportException wse) {
                System.out.println("Caught: " + wse);
                Throwable cause = wse.getCause();
                if (cause != null && (cause instanceof ConnectException || cause instanceof SocketTimeoutException)) {
                    System.out.println("The cause was a timeout exception: " + cause);
                    url = uddiNaming.lookup("UpaBroker");
                    System.out.println(url);
                    BrokerClient b1 = new BrokerClient(url);
                    System.out.println(b1.ping("Client_Backup"));
                }
            }
		}

	}
	/*
	 * 
		System.out.println("Transport ID: "+tv.getId());
		System.out.println("Origin: "+tv.getOrigin());
		System.out.println("Destination: "+tv.getDestination());
		System.out.println("Price: "+tv.getPrice());
		System.out.println("State: "+tv.getState());	
	 * */
}
