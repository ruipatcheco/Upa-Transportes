package pt.upa.transporter;

import pt.upa.transporter.ws.cli.TransporterClient;

public class TransporterClientApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
		}
		String url = "http://localhost:8081/transporter-ws/endpoint";
		
		TransporterClient b = new TransporterClient(url);
		System.out.println(b.ping("test"));

	}
}
