package org.sar.ppi.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Scenario {
	@JsonProperty("$schema")
	private String schema = "";

	@JsonPropertyDescription("The list of Deploy events")
	private Deploy[] deploys = new Deploy[0];

	@JsonPropertyDescription("The list of Undeploy events")
	private Undeploy[] undeploys = new Undeploy[0];

	@JsonPropertyDescription("The list of Call events")
	private Call[] calls = new Call[0];

	@JsonSetter("$schema")
	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setDeploys(Deploy[] deploys) {
		this.deploys = deploys;
	}

	public void setUndeploys(Undeploy[] undeploys) {
		this.undeploys = undeploys;
	}

	public void setCalls(Call[] calls) {
		this.calls = calls;
	}

	public ScheduledEvent[] getEvents() {
		int pos = 0;
		int size = deploys.length + undeploys.length + calls.length;
		ScheduledEvent[] events = new ScheduledEvent[size];
		System.arraycopy(deploys, 0, events, pos, deploys.length);
		pos += deploys.length;
		System.arraycopy(undeploys, 0, events, pos, undeploys.length);
		pos += undeploys.length;
		System.arraycopy(calls, 0, events, pos, calls.length);
		pos += calls.length;
		return events;
	}
}