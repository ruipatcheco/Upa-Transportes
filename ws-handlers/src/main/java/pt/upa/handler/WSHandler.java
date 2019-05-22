package pt.upa.handler;

import pt.upa.X509DigitalSignature;
import pt.upa.ca.ws.cli.CAClient;
import pt.upa.X509CertificateCheck;

import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Set;

import javax.jws.HandlerChain;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PublicKey;
import java.security.cert.Certificate;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;


public class WSHandler implements SOAPHandler<SOAPMessageContext> {
	
	final static String CA_CERTIFICATE_FILE = "ca.cer";
	public static final String REQUEST_PROPERTY = "my.request.property";
	public static final String RESPONSE_PROPERTY = "my.response.property";

	public static final String REQUEST_HEADER = "myRequestHeader";
	public static final String REQUEST_NS = "urn:Handler";

	public static final String RESPONSE_HEADER = "myResponseHeader";
	public static final String RESPONSE_NS = REQUEST_NS;
	
	public static final String URL = "http://localhost:8980/ca-ws/endpoint";
	

	public static final String CLASS_NAME = WSHandler.class.getSimpleName();
	
	
	private CAClient _ca = new CAClient(URL);
	
	public boolean handleMessage(SOAPMessageContext smc) {
		
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (outbound) {
			// outbound message
			
			// get token from response context
			String propertyValue = (String) smc.get(RESPONSE_PROPERTY);
			
			String[] args = propertyValue.split("#");
			String keyStoreFile = args[0];
			String keyStorePassword = args[1] ;
			String keyAlias = args[3];
			String keyPassword= args[4];
			
			
			System.out.printf("%s received '%s'%n", CLASS_NAME, propertyValue);

			// put token in response SOAP header
			try {
				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				

				// add header 
				SOAPHeader sh = se.getHeader();
				if (sh == null)
					sh = se.addHeader();

				
				//ADD SOURCE ELEMENT
				// add header element (name, namespace prefix, namespace)
				Name name = se.createName(REQUEST_HEADER, "e", REQUEST_NS);
				SOAPHeaderElement element = sh.addHeaderElement(name);
				

				//ADD DIGITALSIGNATURE ELEMENT
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				msg.writeTo(stream);
				//Generate digital signature from privatekey
				byte[] digitalSignature =  X509DigitalSignature.makeDigitalSignature(stream.toByteArray(),  X509DigitalSignature.getPrivateKeyFromKeystore(keyStoreFile,
						keyStorePassword.toCharArray(), keyAlias, keyPassword.toCharArray()));
				
				String signedStream = printBase64Binary(digitalSignature);
				
				
				System.out.printf("%s put digitalsignature '%s' on response message header%n", CLASS_NAME,signedStream);
				
				String message = stream.toByteArray().toString();
				
				//ADD TIMESTAMP ELEMENT
				SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				String timeStampString = date.toString();
				element.addTextNode(keyAlias+"#"+signedStream+"#"+message+"#"+timeStampString);

				System.out.printf("%s put timestamp '%s' on response message header%n", CLASS_NAME, timeStampString);

				
			} catch (SOAPException | IOException e) {
				System.out.printf("Failed to add SOAP header because of %s%n", e);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			// inbound message

			// get token from request SOAP header
			try {
				// get SOAP envelope header
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPHeader sh = se.getHeader();
				

				// check header
				if (sh == null) {
					System.out.println("Header not found.");
					return true;
				}

				// get first header element
				Name name = se.createName(REQUEST_HEADER, "e", REQUEST_NS);
				System.out.println(name.getLocalName());
				Iterator it = sh.getChildElements(name);
				
				// check header element
				if (!it.hasNext()) {
					System.out.printf("Source element %s not found.%n", REQUEST_HEADER);
					return true;
				}
				SOAPElement element = (SOAPElement) it.next();

				//get source value
				String rawArgs = element.getValue();
			
				String[] args = rawArgs.split("#");
				//FIXME element.addTextNode(keyAlias+"#"+signedStream+"#"+message+"#"+timeStampString);
				byte[] alias = DatatypeConverter.parseBase64Binary(args[0]);
				byte[] signature = DatatypeConverter.parseBase64Binary(args[1]);
				byte[] message = DatatypeConverter.parseBase64Binary(args[2]);
				byte[] timeStamp = DatatypeConverter.parseBase64Binary(args[3]);
				
				String rawCert = _ca.getCertificate(alias.toString());
				
				byte[] objectBytes = DatatypeConverter.parseBase64Binary(rawCert);
				
				ByteArrayInputStream in = new ByteArrayInputStream(objectBytes);
			    ObjectInputStream is = new ObjectInputStream(in);
			    Certificate certificate = (Certificate) is.readObject();
				
				System.out.printf("%s got '%s'%n", CLASS_NAME, signature.toString());
				
				PublicKey publicKey = certificate.getPublicKey();
				if( !X509DigitalSignature.verifyDigitalSignature(signature, message , publicKey)){
					//Message has been tampered with
					System.out.printf("Message has been tampered with, discarting message...");
					return true;
				}

			} catch (SOAPException | IOException e) {
				System.out.printf("Failed to get SOAP header because of %s%n", e);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		return true;
	}

	public Set<QName> getHeaders() {
		return null;
	}

	public void close(MessageContext messageContext) {
	}

}
