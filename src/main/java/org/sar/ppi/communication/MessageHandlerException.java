package org.sar.ppi.communication;

import org.sar.ppi.PpiException;

/**
 * MessageHandlerException.
 */
public class MessageHandlerException extends PpiException {

	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 */
	public MessageHandlerException(String error) {
		super(error);
	}
}
