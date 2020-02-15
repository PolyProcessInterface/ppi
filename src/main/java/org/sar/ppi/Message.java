package org.sar.ppi;

public abstract class Message {

	private final long idsrc;
	private final long iddest;
	private final int pid;
	
	public long getIdsrc() {
		return idsrc;
	}

	public long getIddest() {
		return iddest;
	}

	public int getPid() {
		return pid;
	}


	public Message(long idsrc, long iddest, int pid) {
		this.idsrc = idsrc;
		this.iddest = iddest;
		this.pid = pid;
	}

}
