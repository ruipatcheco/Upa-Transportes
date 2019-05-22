package pt.upa.broker.exceptions;

abstract class AbstractException extends Exception {
	
	private static final long serialVersionUID = 1L;

    public AbstractException() {
    }

    public AbstractException(String msg) {
        super(msg);
    }
}
