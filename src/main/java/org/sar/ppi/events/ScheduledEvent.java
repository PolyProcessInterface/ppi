package org.sar.ppi.events;

public abstract class ScheduledEvent implements Event {

	private static final long serialVersionUID = 1L;

	protected int node;
	protected int delay;

	public void setNode(int node) {
		this.node = node;
	}

	public int getNode() {
		return node;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getDelay() {
		return delay;
	}
}