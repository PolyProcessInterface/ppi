package org.sar.ppi.mpi;

import org.sar.ppi.Infrastructure;
import org.sar.ppi.Message;
import org.sar.ppi.NodeProcess;
import org.sar.ppi.PpiException;

import mpi.Comm;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

import java.io.*;

/**
 * MpiInfrastructure
 */
public class MpiInfrastructure extends Infrastructure {

	protected boolean running = true;
	protected Comm comm;

	public MpiInfrastructure(NodeProcess process) {
		super(process);
	}

	public void run(String[] args) throws PpiException {
		try {
			MPI.Init(args);
			comm = MPI.COMM_WORLD;
			currentNode = comm.getRank();
			process.start();
			while (running) {
				int[] sizeMsgTab = new int[1];
				Status s = comm.recv(sizeMsgTab, 1, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG);
				int sizeMsg = sizeMsgTab[0];
				byte [] tab = new byte[sizeMsg];
				comm.recv(tab, sizeMsg, MPI.BYTE, s.getSource(), MPI.ANY_TAG);
				// printByteArray(tab);
				Message msg = RetriveMessage(tab);
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
			byte[] tab = ParseMessage(message);
			comm.send(new int[] {tab.length}, 1, MPI.INT, message.getIddest(), 1);
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


	private byte[] ParseMessage(Message message) {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
			 ObjectOutputStream out = new ObjectOutputStream(bos);) {
			out.writeObject(message);
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new PpiException("ERROR OF PARSING");
	}

	private Message RetriveMessage(byte[] message) {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(message);
			 ObjectInput in = new ObjectInputStream(bis);) {
			return (Message) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		throw new PpiException("ERROR OF PARSING");
	}




	protected void printByteArray(byte[] tab){
		System.out.print("[");
		for (int i =0,len=tab.length;i<len;i++){
			System.out.print(tab[i]+" ,");
		}
		System.out.println("]");
	}
}