package org.sar.ppi.events;

public class Undeploy extends ScheduledEvent {
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "undepoy(" + super.toString() + ")";
	}
}
