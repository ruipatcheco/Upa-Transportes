package pt.upa.broker.exceptions;

public class UntrustedMessageException extends AbstractException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UntrustedMessageException() {
        super("Data received is not trusted - Failed Signature Verification");
    }
}
