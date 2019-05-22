package pt.upa.transporter.ws;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.upa.handler.JobHandler;


public class TransporterPortIT {
	TransporterPort _t;
	
	
	
	@Before
	public void setUp() throws Exception {
		_t = new TransporterPort("UpaTransporter1");
	}

	@After
	public void tearDown() throws Exception {
		_t.clearJobs();
	}

	@Test
	public void pingTest() {
		String testText = "Tester";
		String result = _t.ping(testText);
		
		assertEquals("Ping failed","Transporter: " + testText + " ping\n",result);

	}
	
	@Test
	public void successRequestJob() throws BadLocationFault_Exception, BadPriceFault_Exception{
		String origin = "Lisboa";
		String destination = "Coimbra";
		int price = 30;
		
		
		JobView jv = _t.requestJob(origin, destination, price);
		assertNotNull("Returned null JobView",jv);
		
		
		assertEquals("Wrong origin location",origin,jv.getJobOrigin());
		assertEquals("Wrong destination location",destination,jv.getJobDestination());
		assertEquals("Wrong price",price,jv.getJobPrice());
		assertNotNull("Returned null JobViewID",jv.getJobIdentifier());
		assertEquals("Wrong transport state","PROPOSED",jv.getJobState().toString());
		assertNotNull("Returned null companyName",jv.getCompanyName());
	}
	
	@Test( expected = BadLocationFault_Exception.class )
	public void invalidOriginRequestJobTest() throws Exception{
		String origin = "Açores";
		String destination = "Faro";
		int price = 20;
		
		_t.requestJob(origin, destination, price);
	}
	
	@Test( expected = BadLocationFault_Exception.class )
	public void invalidDestinationRequestJobTest() throws Exception{
		String origin = "Lisboa";
		String destination = "Madeira";
		int price = 20;
		
		_t.requestJob(origin, destination, price);
	}
	
	@Test( expected = BadPriceFault_Exception.class )
	public void invalidPriceRequestJobTest() throws Exception{
		String origin = "Lisboa";
		String destination = "Leiria";
		int price = -20;
		
		_t.requestJob(origin, destination, price);
	}
	
	@Test( expected = BadJobFault_Exception.class )
	public void invalidDecideJobTest() throws Exception{
		
		_t.decideJob("olaadeus123zzzzz",true);
	}
	
}
