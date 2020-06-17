package org.sar.ppi.mpi;

import org.sar.ppi.NodeProcess;
import org.sar.ppi.events.Event;

/**
 * MpiProcess class. This Process will wait for messages to arrive from the recvQueue
 * and then execute the NodeProcess handler for each message in a serialThread.
 */
public class MpiProcess implements Runnable {

	protected NodeProcess process;
	protected MpiInfrastructure infra;
	protected String[] args;

	/**
	 * Constructor for MpiProcess.
	 *
	 * @param process a {@link org.sar.ppi.NodeProcess} object.
	 * @param infra a {@link org.sar.ppi.mpi.MpiInfrastructure} object.
	 * @param args list of arguments to pass to the NodeProcess.
	 */
	public MpiProcess(NodeProcess process, MpiInfrastructure infra, String[] args) {
		this.process = process;
		this.infra = infra;
		this.args = args;
	}


	/** {@inheritDoc} */
	@Override
	public void run() {
		process.init(args);
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Event m = infra.recv();
				infra.processEvent(m);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // preserve interruption status
				return;
			}
		}
	}
}
