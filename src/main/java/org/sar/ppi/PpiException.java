package org.sar.ppi;

/**
 * PpiException class.
 */
public class PpiException extends RuntimeException {

	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for PpiException.
	 *
	 * @param e exception that caused it.
	 */
	public PpiException(Exception e) {
		super(e);
	}

	/**
	 * Constructor for PpiException.
	 *
	 * @param msg error message.
	 * @param e exception that caused it.
	 */
	public PpiException(String msg, Exception e) {
		super(msg, e);
	}

    /**
     * Constructor for PpiException.
     *
     * @param error error message.
     */
    public PpiException(String error) { super(error);}
}
