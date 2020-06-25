package org.sar.ppi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

	@JsonPropertyDescription("Map of infrastructure specific configurations")
	private Map<String, Map<String, Object>> infra = new HashMap<>();

	@JsonIgnore
	private String currentInfra = "";

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

	public void setInfra(Map<String, Map<String, Object>> infra) {
		this.infra = infra;
	}

	@JsonIgnore
	@SuppressWarnings("unchecked")
	<T> T getInfraProp(String name, String key, T defaultValue) throws ClassCastException {
		if (infra.containsKey(name) && infra.get(name).containsKey(key)) {
			return (T) infra.get(name).get(key);
		}
		return defaultValue;
	}

	/**
	 * Get one of the config properties specific to the current Infrastructure.
	 *
	 * @param <T>          the return type of the property.
	 * @param key          the key of the property.
	 * @param defaultValue the default value for the property.
	 * @return             the finale value of the property.
	 * @throws ClassCastException if the type of the property is incorrect.
	 */
	@JsonIgnore
	public <T> T getInfraProp(String key, T defaultValue) throws ClassCastException {
		return getInfraProp(currentInfra, key, defaultValue);
	}

	@JsonIgnore
	void setCurrentInfra(String currentInfra) {
		this.currentInfra = currentInfra;
	}

	@JsonIgnore
	public ScheduledEvent[] getEvents() {
		return PpiUtils.concatAll(ScheduledEvent.class, deploys, undeploys, calls);
	}

	@JsonIgnore
	public boolean hasEvents() {
		return deploys.length > 0 || undeploys.length > 0 || calls.length > 0;
	}

	@Override
	public String toString() {
		String d = Arrays.toString(deploys);
		String u = Arrays.toString(undeploys);
		String c = Arrays.toString(calls);
		return "config(deploys:" + d + ", undeploys:" + u + ", calls:" + c + ")";
	}
}
