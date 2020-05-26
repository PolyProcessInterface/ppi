package org.sar.ppi.mpi;

import org.sar.ppi.NodeProcess;
import org.sar.ppi.communication.Message;

/**
 * MpiProcess class. This Process will wait for messages to arrive from the recvQueue
 * and then execute the NodeProcess handler for each message in a serialThread.
 */
public class MpiProcess implements Runnable {

	protected NodeProcess process;
	protected MpiInfrastructure infra;

	/**
	 * Constructor for MpiProcess.
	 *
	 * @param process a {@link org.sar.ppi.NodeProcess} object.
	 * @param infra a {@link org.sar.ppi.mpi.MpiInfrastructure} object.
	 */
	public MpiProcess(NodeProcess process, MpiInfrastructure infra) {
		this.process = process;
		this.infra = infra;
	}


	/** {@inheritDoc} */
	@Override
	public void run() {
		process.start();
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Message m = infra.recv();
				infra.serialThreadRun(() -> process.processMessage(m));
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // preserve interruption status
				return;
			}
		}
	}
}
