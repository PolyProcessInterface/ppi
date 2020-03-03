package org.sar.ppi.mpi;

import org.sar.ppi.Infrastructure;
import org.sar.ppi.Message;
import org.sar.ppi.NodeProcess;
import org.sar.ppi.PpiException;

import mpi.Comm;
import mpi.MPI;
import mpi.MPIException;

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

	public void run(String[] args) throws PpiException {
		try {
			MPI.Init(args);
			comm = MPI.COMM_WORLD;
			currentNode = comm.getRank();
			process.start();
			while (running) {
				byte [] tab = new byte[MaxSize];
				comm.recv(tab, MaxSize, MPI.BYTE, MPI.ANY_SOURCE, MPI.ANY_TAG);
				//System.out.println("tableau recus= ");
				printByteArray(tab);
				Message msg = ContentHandler.RetriveMessage(tab);
				process.processMessage(msg);
			}
			MPI.Finalize();
		} catch (MPIException e) {
			throw new PpiException("Init fail.", e);
		}
	}

	@Override
	public void send(Message message) throws PpiException{
		try {
			byte[] tab = ContentHandler.ParseMessage(message);
			//System.out.println("Object envoyer= "+message);
			comm.send(tab, tab.length, MPI.BYTE, message.getIddest(), 1);
		} catch (MPIException e) {
			throw new PpiException("Send to" + message.getIddest() + "failed", e);
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