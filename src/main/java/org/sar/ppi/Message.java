package org.sar.ppi;

import java.io.Serializable;

public abstract class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	private final int idsrc;
	private final int iddest;
	
	public int getIdsrc() {
		return idsrc;
	}

	public int getIddest() {
		return iddest;
	}

	public Message(int idsrc, int iddest) {
		this.idsrc = idsrc;
		this.iddest = iddest;
	}

}
