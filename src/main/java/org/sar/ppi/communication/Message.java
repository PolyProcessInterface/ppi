package org.sar.ppi.communication;

import java.io.Serializable;

/**
 * Abstract Message class.
 */
public abstract class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	private final int idsrc;
	private final int iddest;
	
	/**
	 * Getter for the field <code>idsrc</code>.
	 *
	 * @return a int.
	 */
	public int getIdsrc() {
		return idsrc;
	}

	/**
	 * Getter for the field <code>iddest</code>.
	 *
	 * @return a int.
	 */
	public int getIddest() {
		return iddest;
	}

	/**
	 * Constructor for Message.
	 *
	 * @param idsrc id of the source.
	 * @param iddest id of the destination.
	 */
	public Message(int idsrc, int iddest) {
		this.idsrc = idsrc;
		this.iddest = iddest;
	}

}
