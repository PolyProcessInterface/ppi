package org.sar.ppi;

public class ObjectMessage extends Message {

	protected Object content;
	
	public ObjectMessage(long idsrc, long iddest, int pid, Object content) {
		super(idsrc, iddest, pid);
		this.content=content;
	}

	public Object getContent() {
		return this.content;
	}
}
