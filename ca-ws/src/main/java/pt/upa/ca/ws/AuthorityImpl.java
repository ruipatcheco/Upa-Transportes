package pt.upa.ca.ws;

import pt.upa.X509CertificateCheck;
import pt.upa.X509DigitalSignature;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.util.AbstractMap;
import java.util.HashMap;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.bind.DatatypeConverter;

import pt.upa.ca.ws.exceptions.UnavaliableCertificateException;

@WebService(endpointInterface = "pt.upa.ca.ws.Authority")
@HandlerChain(file = "/ca_handler-chain.xml")
public class AuthorityImpl implements Authority {
	
	AbstractMap<String,Certificate> _certificates;
	
	final static String KEY_LOCATION = "src/main/resources/keys/";
	
	AuthorityImpl() throws Exception{
		_certificates = new HashMap<String,Certificate>();
		
		_certificates.put("UpaBroker", X509DigitalSignature.readCertificateFile(KEY_LOCATION+"UpaBroker.cer"));
		_certificates.put("UpaTransporter1", X509DigitalSignature.readCertificateFile(KEY_LOCATION+"UpaTransporter1.cer"));
		_certificates.put("UpaTransporter2", X509DigitalSignature.readCertificateFile(KEY_LOCATION+"UpaTransporter2.cer"));
	}

	
	@Override
	public String getCertificate(String name) throws UnavaliableCertificateException{
		Certificate certificate = _certificates.get(name);
		
		if(certificate == null)
			throw new UnavaliableCertificateException(name);
		
		String encodedCert = null;
		
		try {
			encodedCert = DatatypeConverter.printBase64Binary(certificate.getEncoded());
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return encodedCert;
	}	
	
}
