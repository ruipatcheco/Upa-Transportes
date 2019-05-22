/**
 * 
 */
package pt.upa.broker.ws.it;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.upa.broker.BrokerClientApplication;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportStateView;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;
import pt.upa.broker.ws.cli.BrokerClient;

/**
 * @author tcrosado
 *
 */
public class ClientIT {

	BrokerClient _client;
	
	
    @BeforeClass
    public static void oneTimeSetUp() {
    	
    }

    @AfterClass
    public static void oneTimeTearDown() {

    }	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		_client = new BrokerClient("http://localhost:8080/broker-ws/endpoint");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		_client = null;
	}
	
	
	@Test
	public void pingTest() {
		String testText = "Tester";
		String result = _client.ping(testText);
		
		assertEquals("Ping failed","Broker: " + testText + " ping\n",result);
	}

	/*@Test
	public void normalRequestTransportTest() throws Exception{
	
		String origin = "Lisboa";
		String destination = "Faro";
		int price = 20;
		String id =_client.requestTransport(origin, destination, price);
		assertNotNull("Returned id in null",id);
		
		TransportView transportResponse = _client.viewTransport(id);
		String resultOrigin = transportResponse.getOrigin();
		String resultDestination = transportResponse.getDestination();
		String resultId = transportResponse.getId();
		int resultPrice = transportResponse.getPrice();
		assertEquals("Wrong origin location",origin,resultOrigin);
		assertEquals("Wrong origin location",destination,resultDestination);
		assertEquals("Wrong price",price,resultPrice);
		assertEquals("Not the same Id on TransportView",id,resultId);
	}
	
	@Test( expected = UnknownLocationFault_Exception.class )
	public void invalidOriginRequestTransportTest() throws Exception{
		String origin = "Açores";
		String destination = "Faro";
		int price = 20;
		
		_client.requestTransport(origin, destination, price);
	}
	
	@Test( expected = UnknownLocationFault_Exception.class )
	public void invalidDestinationRequestTransportTest() throws Exception{
		String origin = "Lisboa";
		String destination = "Açores";
		int price = 20;
		
		_client.requestTransport(origin, destination, price);
	}
	
	@Test( expected = UnavailableTransportFault_Exception.class)
	public void unavaliableTransportCoverageTest() throws Exception{
		String origin = "Porto";
		String destination = "Faro";
		int price = 20;
		
		_client.requestTransport(origin, destination, price);
	}
	
	@Test( expected = InvalidPriceFault_Exception.class)
	public void negativePriceRequestTest() throws Exception{
		String origin = "Porto";
		String destination = "Lisboa";
		int price = -1;
		
		_client.requestTransport(origin, destination, price);
	}

	@Test( expected = UnknownTransportFault_Exception.class)
	public void viewUnknownTransportTest() throws Exception{
		_client.viewTransport("12334");
	}

	@Test
	public void viewTransportTest() throws Exception{
		String origin = "Porto";
		String destination = "Lisboa";
		int price = 20;
		
		String id = _client.requestTransport(origin, destination, price);
		
		TransportView transport = _client.viewTransport(id);
		String resultOrigin = transport.getOrigin();
		String resultDestination = transport.getDestination();
		String resultId = transport.getId();
		int resultPrice = transport.getPrice();
		
		assertEquals("Wrong origin location",origin,resultOrigin);
		assertEquals("Wrong origin location",destination,resultDestination);
		assertEquals("Wrong price",price,resultPrice);
		assertEquals("Not the same Id on TransportView",id,resultId);
	}*/

	
	

}
