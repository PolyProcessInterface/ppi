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
	public final int MaxSize;
	public MpiInfrastructure(NodeProcess process) {
		super(process);
		MaxSize = 1024;
	}

	public MpiInfrastructure(NodeProcess process,int max_message_lenth) {
		super(process);
		MaxSize = max_message_lenth;
	}

	@Override
	public void run(String[] args) throws PpiException {
		try {
			MPI.Init(args);
			comm = MPI.COMM_WORLD;
			currentNode = comm.getRank();
			process.start();
			while (running) {
				Object buf;
				byte [] tab = new byte[MaxSize];
				Status status = comm.recv(tab, MaxSize, MPI.BYTE, MPI.ANY_SOURCE, MPI.ANY_TAG);
				//System.out.println("tableau recus= ");
				printByteArray(tab);
				buf =ContentHandler.RetriveObject(tab);
			//	System.out.println("buff apres= "+buf);
				process.processMessage(status.getSource(), buf);
			}
			MPI.Finalize();
		} catch (MPIException e) {
			throw new PpiException("Init fail.", e);
		}
	}

	@Override
	public void send(int dest, Object message)  throws PpiException{
		try {
			byte [] tab = ContentHandler.ParseObject(message);
			//System.out.println("Object envoyer= "+message);
			comm.send(tab, tab.length, MPI.BYTE, dest, 1);
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

	private void printByteArray(byte[] tab){
		System.out.print("[");
		for (int i =0,len=tab.length;i<len;i++){
			System.out.print(tab[i]+" ,");
		}
		System.out.println("]");
	}
}