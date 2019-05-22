package pt.upa.ca;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.security.cert.Certificate;

import javax.xml.bind.DatatypeConverter;

import pt.upa.ca.ws.cli.CAClient;

public class CAClientApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(CAClientApplication.class.getSimpleName() + " starting...");
		//check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
		}
		String url = "http://localhost:8980/ca-ws/endpoint";
		
		CAClient b = new CAClient(url);
		byte[] bytes = DatatypeConverter.parseBase64Binary(b.getCertificate("UpaBroker"));
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = new ObjectInputStream(bis);
		Certificate cert = (Certificate) in.readObject();
		System.out.print(b.getCertificate("UpaBroker"));
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
