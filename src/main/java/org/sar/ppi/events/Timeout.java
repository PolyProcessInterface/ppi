package org.sar.ppi.events;

public class Timeout extends ScheduledEvent {
	private static final long serialVersionUID = 1L;
	protected long threadId;

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	public long getThreadId() {
		return threadId;
	}

	@Override
	public String toString() {
		return "Timout(" + super.toString() + ", threadId:" + threadId + ")";
	}
}
