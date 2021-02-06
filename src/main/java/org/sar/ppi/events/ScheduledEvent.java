package org.sar.ppi.events;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public abstract class ScheduledEvent implements Event {
	private static final long serialVersionUID = 1L;

	@JsonPropertyDescription("The id of the node that will execute the event")
	protected int node;

	@JsonPropertyDescription("The delay before the event execution")
	protected long delay;

	public void setNode(int node) {
		this.node = node;
	}

	public int getNode() {
		return node;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public long getDelay() {
		return delay;
	}

	@Override
	public String toString() {
		return "node:" + node + ", delay:" + delay;
	}
}
