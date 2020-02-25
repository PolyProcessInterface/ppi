package org.sar.ppi;

import mpi.Comm;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

/**
 * MpiInfrastructure
 */
public class MpiInfrastructure extends Infrastructure {

	protected boolean running = true;
	protected Comm comm;

	public MpiInfrastructure(Protocol protocol) {
		super(protocol);
	}

	@Override
	public void run(String[] args) throws PpiException {
		try {
			MPI.Init(args);
			comm = MPI.COMM_WORLD;
			currentNode = comm.getRank();
			protocol.startNode(currentNode);
			while (running) {
				Object buf = new Object();
				Status status = comm.recv(buf, 0, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
				protocol.processMessage(status.getSource(), buf);
			}
			MPI.Finalize();
		} catch (MPIException e) {
			throw new PpiException("Init fail.", e);
		}
	}

	@Override
	public void send(int dest, Object message)  throws PpiException{
		//juste pour faire des teste
		try {
			//pour le message y a MPI.BYTE qui peux etre intéressant
			comm.send(null, 0, MPI.INT, dest, 1);
		} catch (MPIException e) {
			throw new PpiException("Send to" + dest + "failed", e);
		}
	}

	@Override
	public void exit() {
		running = false;
	}

	@Override
	public int size() {
		try {
			return comm.getSize();
		} catch (MPIException e) {
			throw new PpiException("Fail to get size.", e);
		}
	}
}