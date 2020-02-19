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
			currentNode = new MpiNode(comm.getRank());
			protocol.startNode(currentNode);
			while (running) {
				Object buf = new Object();
				Status status = comm.recv(buf, 1, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
				protocol.processMessage(new MpiNode(status.getSource()), buf);
			}
			MPI.Finalize();
		} catch (MPIException e) {
			throw new PpiException("Init fail.", e);
		}
	}

	@Override
	public void send(Node dest, Object message)  throws PpiException{
		//juste pour faire des teste
		try {
			//pour le message y a MPI.BYTE qui peux etre intéressant
			comm.send(message,1,MPI.INT,dest.getId(),MPI.ANY_TAG);
		} catch (MPIException e) {
			throw new PpiException("Send to"+dest.getId()+"failed",e);
		}
	}
	//Je bloquer sur le reste j'ai fait ça
	@Override
	public void broadcast(Object messsage) {
		int rank =currentNode.getId();
		int i= rank+1;
		int nbp = size();
		while(i!=rank){
			try {
				comm.send(messsage,1,MPI.INT,i,MPI.ANY_TAG);
				i=(i+1)%nbp;
			} catch (MPIException e) {
				throw new PpiException("Send to"+i+"failed",e);
			}
		}
	}
	@Override
	public void exit() {
		running = false;
	}

	@Override
	public Node getNode(int id) {
		return ( id<size() ? new MpiNode(id) : null);
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