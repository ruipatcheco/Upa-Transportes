package pt.upa.ca.ws.exceptions;

public class UnavaliableCertificateException extends AbstractException {

	private static final long serialVersionUID = 1L;
	private String _name;
	
	public UnavaliableCertificateException(String name){
		_name=name;
	}
	
	@Override
	public String getMessage(){
		return "Certificate of "+_name+" is unavaliable";
		
	}

}
