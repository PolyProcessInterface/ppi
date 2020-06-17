package org.sar.ppi.events;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public abstract class ScheduledEvent implements Event {

	private static final long serialVersionUID = 1L;

	@JsonPropertyDescription("The id of the node that will execute the event")
	protected int node;
	@JsonPropertyDescription("The delay before the event execution")
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