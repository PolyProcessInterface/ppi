package org.sar.ppi.mpi;

import org.sar.ppi.NodeProcess;

public class MpiProcess implements Runnable {

	protected NodeProcess process;
	protected MpiInfrastructure infra;

	public MpiProcess(NodeProcess process, MpiInfrastructure infra) {
		this.process = process;
		this.infra = infra;
	}


	@Override
	public void run() {
		process.start();
		while (infra.running.get()) {
			try {
				process.processMessage(infra.recv());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}