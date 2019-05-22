package pt.upa.ca.ws.exceptions;

abstract class AbstractException extends Exception {
	
	private static final long serialVersionUID = 1L;

    public AbstractException() {
    }

    public AbstractException(String msg) {
        super(msg);
    }
}
