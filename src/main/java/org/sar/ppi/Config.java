package org.sar.ppi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.Arrays;
import org.sar.ppi.events.Call;
import org.sar.ppi.events.Deploy;
import org.sar.ppi.events.ScheduledEvent;
import org.sar.ppi.events.Undeploy;
import org.sar.ppi.tools.PpiUtils;

public class Config {
	@JsonProperty(value = "$schema", access = JsonProperty.Access.WRITE_ONLY)
	@SuppressWarnings("PMD.UnusedPrivateField")
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

	public Deploy[] getDeploys() {
		return deploys;
	}

	public void setUndeploys(Undeploy[] undeploys) {
		this.undeploys = undeploys;
	}

	public Undeploy[] getUndeploys() {
		return undeploys;
	}

	public void setCalls(Call[] calls) {
		this.calls = calls;
	}

	public Call[] getCalls() {
		return calls;
	}

	@JsonIgnore
	public ScheduledEvent[] getEvents() {
		return PpiUtils.concatAll(ScheduledEvent.class, deploys, undeploys, calls);
	}

	@JsonIgnore
	public boolean isEmpty() {
		return deploys.length == 0 && undeploys.length == 0 && calls.length == 0;
	}

	@Override
	public String toString() {
		String d = Arrays.toString(deploys);
		String u = Arrays.toString(undeploys);
		String c = Arrays.toString(calls);
		return "config(deploys:" + d + ", undeploys:" + u + ", calls:" + c + ")";
	}
}
