package pt.upa.transporter.ws;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.upa.handler.JobHandler;

public class TransporterPortTest {
	TransporterPort _t1;
	TransporterPort _t2;
	JobHandler _j1;
	JobHandler _j2;
	
	
	
	@Test
	public void successPriceOffer_OddPriceOddTransporter() throws BadLocationFault_Exception, BadPriceFault_Exception {
		_j1 = _t1.getHandler();
		
		String origin = "Lisboa";
		String destination = "Leiria";
		int priceIMPAR = 21;
		
		//preco impar + num transportadora impar -> oferta abaixo
		JobView ii = _t1.requestJob(origin, destination, priceIMPAR);
		assertNotNull("Returned null JobView",ii);
		assertTrue("Offered price is not lower than offered",ii.getJobPrice()<priceIMPAR);
		
		}
	
	@Test
	public void successPriceOffer_OddPriceEvenTransporter() throws BadLocationFault_Exception, BadPriceFault_Exception {
		_j2 = _t2.getHandler();
		
		String origin = "Lisboa";
		String destination = "Leiria";
		int priceIMPAR = 21;
		
		//preco impar + num transportadora par -> oferta acima
		JobView ip = _t2.requestJob(origin, destination, priceIMPAR);
		assertNotNull("Returned null JobView",ip);
		assertTrue("Offered price is not higher than offered",ip.getJobPrice()>priceIMPAR);		
	}
	
	@Test
	public void successPriceOffer_EvenPriceEvenTransporter() throws BadLocationFault_Exception, BadPriceFault_Exception {
		_j2 = _t2.getHandler();
		
		String origin = "Lisboa";
		String destination = "Leiria";
		int pricePAR = 20;
		
		//preco par + num transportadora par -> oferta abaixo
		JobView pp = _t2.requestJob(origin, destination, pricePAR);
		assertNotNull("Returned null JobView",pp);
		assertTrue("Offered price is not lower than offered",pp.getJobPrice()<pricePAR);
	}
	
	@Test
	public void successPriceOffer_EvenPriceOddTransporter() throws BadLocationFault_Exception, BadPriceFault_Exception {

		_j1 = _t1.getHandler();
		
		String origin = "Lisboa";
		String destination = "Leiria";
		int pricePAR = 20;

		//preco par + num transportadora impar -> oferta acima
		JobView pi = _t1.requestJob(origin, destination, pricePAR);
		assertNotNull("Returned null JobView",pi);
		
		assertTrue("Offered price is not higher than offered",pi.getJobPrice()>pricePAR);
	}

	
	@Before
	public void setUp() throws Exception {
		_t1 = new TransporterPort("UpaTransporter1");
		_t2 = new TransporterPort("UpaTransporter2");
	}

	@After
	public void tearDown() throws Exception {
		_t1.clearJobs();
		_t2.clearJobs();
	}


}
