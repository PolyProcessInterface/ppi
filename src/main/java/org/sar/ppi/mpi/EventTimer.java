package org.sar.ppi.mpi;

import java.io.Serializable;
import java.util.TimerTask;

import org.sar.ppi.events.ScheduledEvent;

public class EventTimer extends TimerTask implements Serializable {
	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;
	private MpiInfrastructure infra;
	private ScheduledEvent e;

	public EventTimer(MpiInfrastructure infra, ScheduledEvent e) {
		this.infra = infra;
		this.e = e;
	}

	@Override
	public void run() {
		infra.processEvent(e);
	}
}