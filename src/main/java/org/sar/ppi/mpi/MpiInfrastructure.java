package org.sar.ppi.mpi;

import org.sar.ppi.*;

import mpi.Comm;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;
import org.sar.ppi.simulator.peersim.ProtocolTools;


import java.io.*;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
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
	protected Timer timer = new Timer();
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
			while (running.get() || !sendQueue.isEmpty()) {
				Status s = comm.iProbe(MPI.ANY_SOURCE, MPI.ANY_TAG);
				if (s != null) {
					recvMpi(s.getSource(), s.getTag());
				}
				Message m = sendQueue.poll();
				if (m != null) {
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
			Message msg =RetriveMessage(tab);
			if(msg instanceof  SchedMessage) {
				SchedMessage shed = (SchedMessage) msg;
				timer.schedule(new ScheduledFunction(shed.getName(),shed.getArgs(),process),shed.getDelay());
			}else
				recvQueue.add(msg);
		} catch (MPIException e) {
			throw new PpiException("Receive from" + source + "failed", e);
		}
	}

	protected void sendMpi(Message message) throws PpiException {
		try {
			byte[] tab = ParseMessage(message);
			int[] sizes = {tab.length};
			comm.send(sizes, 1, MPI.INT, message.getIddest(), 1);
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
		if(timer!=null)
			timer.cancel();
		running.set(false);
	}
	public  void launchSimulation(String path){
		List<Object[]> l_call = ProtocolTools.readProtocolJSON(path);
		int num_node;
		for(Object[] func : l_call){
			num_node=(int)func[1];
			if(num_node==currentNode)
				timer.schedule(new ScheduledFunction((String)func[0],Arrays.copyOfRange(func,3,func.length),process),(long)func[2]);
			else
				send(new SchedMessage(currentNode,num_node,(String) func[0],(long)func[2],Arrays.copyOfRange(func,3,func.length)));
		}
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