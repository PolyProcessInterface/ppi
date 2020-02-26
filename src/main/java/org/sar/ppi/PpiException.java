package org.sar.ppi;

/**
 * PpiException
 */
public class PpiException extends RuntimeException {

	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;

	public PpiException(Exception e) {
		super(e);
	}

	public PpiException(String msg, Exception e) {
		super(msg, e);
	}

    public PpiException(String error) { super(error);}
}