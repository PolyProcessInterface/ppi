package org.sar.ppi.dispatch;

import org.sar.ppi.PpiException;

/**
 * MessageHandlerException.
 */
public class DispatcherException extends PpiException {
	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 */
	public DispatcherException(String error) {
		super(error);
	}
}
