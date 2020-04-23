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
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MpiInfrastructure
 */
public class MpiInfrastructure extends Infrastructure {

	AtomicBoolean running = new AtomicBoolean(true);
	protected Comm comm;
	protected Queue<Message> sendQueue;
	protected BlockingQueue<Message> recvQueue;
	protected Thread executor;

	public MpiInfrastructure(NodeProcess process) {
		super(process);
		sendQueue = new ConcurrentLinkedQueue<>();
		recvQueue = new LinkedBlockingQueue<>();
		executor = new Thread(new MpiProcess(process, this));
	}

	public void run(String[] args) throws PpiException {
		try {
			MPI.InitThread(args, MPI.THREAD_FUNNELED);
			comm = MPI.COMM_WORLD;
			currentNode = comm.getRank();
			executor.start();
			// System.out.printf("P%d> Entering main loop\n", getId());
			while (running.get()) {
				Status s = comm.iProbe(MPI.ANY_SOURCE, MPI.ANY_TAG);
				if (s != null) {
					// System.out.printf("P%d> Receiving message\n", getId());
					recvMpi(s.getSource(), s.getTag());
				}
				Message m = sendQueue.poll();
				if (m != null) {
					// System.out.printf("P%d> Sending message %s\n", getId(), m.toString());
					sendMpi(m);
				}
			}
			MPI.Finalize();
		} catch (MPIException e) {
			throw new PpiException("Init fail.", e);
		}
	}

	protected void recvMpi(int source, int tag) throws PpiException {
		try {
			int[] sizeMsgTab = new int[1];
			comm.recv(sizeMsgTab, 1, MPI.INT, source, tag);
			int sizeMsg = sizeMsgTab[0];
			byte [] tab = new byte[sizeMsg];
			comm.recv(tab, sizeMsg, MPI.BYTE, source, tag);
			// printByteArray(tab);
			recvQueue.add(RetriveMessage(tab));
		} catch (MPIException e) {
			throw new PpiException("Receive from" + source + "failed", e);
		}
	}

	protected void sendMpi(Message message) throws PpiException {
		try {
			byte[] tab = ParseMessage(message);
			comm.send(new int[] {tab.length}, 1, MPI.INT, message.getIddest(), 1);
			comm.send(tab, tab.length, MPI.BYTE, message.getIddest(), 1);
		} catch (MPIException e) {
			throw new PpiException("Send to" + message.getIddest() + "failed", e);
		}
	}

	public Message recv() throws InterruptedException {
		return recvQueue.take();
	}

	@Override
	public void send(Message message) {
		sendQueue.add(message);
	}

	@Override
	public void exit() {
		running.set(false);
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