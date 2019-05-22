package pt.upa.ca.ws;

import javax.jws.WebService;

import pt.upa.ca.ws.exceptions.UnavaliableCertificateException;
@WebService
public interface Authority {
	 
	public String getCertificate(String name) throws UnavaliableCertificateException;
	
}
