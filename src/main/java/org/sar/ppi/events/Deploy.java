package org.sar.ppi.events;

public class Deploy extends ScheduledEvent {
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "depoy(" + super.toString() + ")";
	}
}
