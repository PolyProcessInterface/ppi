package org.sar.ppi.mpi;

import org.sar.ppi.NodeProcess;

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
		while (infra.running.get()) {
			infra.serialThreadRun(() -> {
				try {
					process.processMessage(infra.recv());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}
	}
}
