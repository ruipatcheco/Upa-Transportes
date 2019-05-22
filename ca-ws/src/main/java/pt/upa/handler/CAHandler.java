package pt.upa.handler;

import pt.upa.X509DigitalSignature;
import pt.upa.X509CertificateCheck;

import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Set;

import javax.jws.HandlerChain;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;


public class CAHandler implements SOAPHandler<SOAPMessageContext> {
	
	public static final String REQUEST_PROPERTY = "my.request.property";
	public static final String RESPONSE_PROPERTY = "my.response.property";

	public static final String REQUEST_HEADER = "myRequestHeader";
	public static final String REQUEST_NS = "urn:CA";

	public static final String RESPONSE_HEADER = "myResponseHeader";
	public static final String RESPONSE_NS = REQUEST_NS;
	

	public static final String CLASS_NAME = CAHandler.class.getSimpleName();
	
	public static final String KEYSTORE_FILE = "src/resources/CA.jks";
	public static final String KEYSTORE_PASSWORD = "1secure";
	public static final String KEY_ALIAS = "CA";
	public static final String KEY_PASSWORD = "ins3cur3";
	public static final String TOKEN = "CAServer";

	public boolean handleMessage(SOAPMessageContext smc) {
		
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (outbound) {
			// outbound message
			
			// get token from response context
			String propertyValue = (String) smc.get(RESPONSE_PROPERTY);
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
				Name name = se.createName(RESPONSE_HEADER, "e", RESPONSE_NS);
				SOAPHeaderElement element = sh.addHeaderElement(name);

				// add header element value
				element.addTextNode(TOKEN);
				
				System.out.printf("%s put token '%s' on response message header%n", CLASS_NAME, TOKEN);

				//ADD DIGITALSIGNATURE ELEMENT
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				msg.writeTo(stream);
				//Generate digital signature from privatekey
				byte[] digitalSignature =  X509DigitalSignature.makeDigitalSignature(stream.toByteArray(),  X509DigitalSignature.getPrivateKeyFromKeystore(KEYSTORE_FILE,
						KEYSTORE_PASSWORD.toCharArray(), KEY_ALIAS, KEY_PASSWORD.toCharArray()));
				
				String digitalSignatureString = printBase64Binary(digitalSignature);
				element.addTextNode(digitalSignatureString);

				System.out.printf("%s put digitalsignature '%s' on response message header%n", CLASS_NAME,digitalSignatureString);

				//ADD TIMESTAMP ELEMENT
				SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				String timeStampString = date.toString();
				element.addTextNode(timeStampString);

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
				Iterator it = sh.getChildElements(name);
				// check header element
				if (!it.hasNext()) {
					System.out.printf("Source element %s not found.%n", REQUEST_HEADER);
					return true;
				}
				SOAPElement element = (SOAPElement) it.next();

				//get source value
				String source = element.getValue();
				System.out.printf("%s got '%s'%n", CLASS_NAME, source);
				
				String certificatePath = source +".cer";
				
				if (!it.hasNext()) {
					System.out.printf("DigitalSignature element %s not found.%n", REQUEST_HEADER);
					return true;
				}
				
				//get digital signature 
				element = (SOAPElement) it.next();
				String digitalSignature = element.getValue();
				System.out.printf("%s got '%s'%n", CLASS_NAME, digitalSignature);
				
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				msg.writeTo(stream);
				
				Certificate certificate =  X509DigitalSignature.readCertificateFile(certificatePath);
				PublicKey publicKey = certificate.getPublicKey();
				if( !X509DigitalSignature.verifyDigitalSignature(parseBase64Binary(digitalSignature), stream.toByteArray() , publicKey)){
					//Message has been tampered with
					System.out.printf("Message has been tampered with, discarting message...");
					return true;
				}

				//get timestamp
				if (!it.hasNext()) {
					System.out.printf(" element %s not found.%n", REQUEST_HEADER);
					return true;
				}
				element = (SOAPElement) it.next();

				//get source value
				String timestamp = element.getValue();
				System.out.printf("%s got '%s'%n", CLASS_NAME, timestamp);
				
				//falta
				//if(timestamp not in timestamps recebidos){
					

				

				/*
				// *** #5 ***
				// put token in request context
				String newValue = headerValue + "," + _token;
				System.out.printf("%s put token '%s' on request context%n", CLASS_NAME, TOKEN);
				smc.put(REQUEST_PROPERTY, newValue);
				// set property scope to application so that server class can
				// access property
				smc.setScope(REQUEST_PROPERTY, Scope.APPLICATION);
				*/

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
