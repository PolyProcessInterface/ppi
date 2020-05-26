package org.sar.ppi.mpi;

import org.sar.ppi.*;

import mpi.Comm;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;
import org.sar.ppi.communication.AppMessage.SchedMessage;
import org.sar.ppi.communication.AppMessage.ShedBreakMessage;
import org.sar.ppi.communication.AppMessage.ShedOnMessage;
import org.sar.ppi.communication.Message;
import org.sar.ppi.communication.Tasks.SchedDeploy;
import org.sar.ppi.communication.Tasks.ScheduledBreakDown;
import org.sar.ppi.communication.Tasks.ScheduledFunction;
import org.sar.ppi.tools.ProtocolTools;


import java.io.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * MpiInfrastructure class.
 */
public class MpiInfrastructure extends Infrastructure {

	AtomicBoolean running = new AtomicBoolean(true);
	protected Comm comm;
	protected Queue<Message> sendQueue = new ConcurrentLinkedQueue<>();
	protected BlockingQueue<Message> recvQueue = new LinkedBlockingQueue<>();
	protected Thread executor;
	protected String FileName = "no";
	/**
	 * Constructor for MpiInfrastructure.
	 *
	 * @param process a {@link org.sar.ppi.NodeProcess} object.
	 */
	public MpiInfrastructure(NodeProcess process) {
		super(process);
		executor = new Thread(new MpiProcess(process, this));
	}

	public MpiInfrastructure(NodeProcess process,String scenario) {
		this(process);
		System.out.println(scenario);
		FileName=scenario;
		System.out.println(FileName);
	}


	/**
	 * Run the infrastructure.
	 *
	 * @param args an array of {@link java.lang.String} objects.
	 * @throws org.sar.ppi.PpiException if any.
	 */
	public void run(String[] args) throws PpiException {
		try {
			MPI.InitThread(args, MPI.THREAD_FUNNELED);
			comm = MPI.COMM_WORLD;
			currentNode = comm.getRank();
			executor.start();
			if(!FileName.equals("no"))
				get_my_tasks(FileName);
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
			for (Thread t : threads.values()) {
				t.interrupt();
				System.out.printf("%d Interrupted waiting thread %d\n", getId(), t.getId());
				t.join();
				System.out.printf("%d Interrupted waiting thread %d\n", getId(), t.getId());
			}
			executor.interrupt();
			System.out.printf("%d Interrupted MpiProcess thread\n", getId());
			executor.join();
			System.out.printf("%d Joined MpiProcess thread\n", getId());
			MPI.Finalize();
		} catch (MPIException e) {
			throw new PpiException("Init fail.", e);
		} catch (InterruptedException e) {
			throw new PpiException("Interrupted while waiting to join MpiProcess", e);
		}
	}

	/**
	 * Fetch a message from MPI and add it the ce <code>recvQueue</code>.
	 * This function is blocking.
	 *
	 * @param source the MPI source of the message.
	 * @param tag the MPI tag of the message.
	 * @throws org.sar.ppi.PpiException on MpiException.
	 */
	protected void recvMpi(int source, int tag) throws PpiException {
		try {
			int[] sizeMsgTab = new int[1];
			comm.recv(sizeMsgTab, 1, MPI.INT, source, tag);
			int sizeMsg = sizeMsgTab[0];
			byte [] tab = new byte[sizeMsg];
			comm.recv(tab, sizeMsg, MPI.BYTE, source, tag);
			Message msg = RetriveMessage(tab);
			recvQueue.add(msg);
		} catch (MPIException e) {
			throw new PpiException("Receive from" + source + "failed", e);
		}
	}

	/**
	 * Send a message via MPI. This function is blocking.
	 *
	 * @param message the {@link Message} object to send.
	 * @throws org.sar.ppi.PpiException if any.
	 */
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

	/**
	 * Fetch a message from the <code>recvQueue</code>. This function is blocking.
	 *
	 * @return a {@link Message} object.
	 * @throws java.lang.InterruptedException if interrupted while waiting.
	 */
	public Message recv() throws InterruptedException {
		return recvQueue.take();
	}

	/** {@inheritDoc} */
	@Override
	public void send(Message message) {
		sendQueue.add(message);
	}

	/** {@inheritDoc} */
	@Override
	public void exit() {
		process.stopSched();
		running.set(false);
	}

	public void get_my_tasks(String path){
		HashMap<String,List<Object[]>> map = ProtocolTools.readProtocolJSON(path);
		List<Object[]> l_call = map.get("events");
		int num_node;
		for(Object[] func : l_call){
		    System.out.println(22);
			num_node=(int)func[1];
			if(process==null)
				System.out.println("process null");
			if(process.getTimer() == null)
				System.out.println("MERDE");
			if(num_node==currentNode)
				process.getTimer().schedule(new ScheduledFunction((String)func[0],Arrays.copyOfRange(func,3,func.length),process,this),(long)func[2]);
		}
		l_call=map.get("Off");
		for(Object[] func : l_call){
			num_node=(int)func[0];
			if(num_node==currentNode)
				process.getTimer().schedule(new ScheduledBreakDown(process),(long)func[1]);
		}
		l_call=map.get("On");
		for(Object[] func : l_call){
			num_node=(int)func[0];
			if(num_node==currentNode)
				process.getTimer().schedule(new SchedDeploy(this),(long)func[1]);
		}
	}

	/** {@inheritDoc} */
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



	/**
	 * Debug print an array of bytes.
	 *
	 * @param tab an array of {@link byte} objects.
	 */
	protected void printByteArray(byte[] tab){
		System.out.print("[");
		for (int i =0,len=tab.length;i<len;i++){
			System.out.print(tab[i]+" ,");
		}
		System.out.println("]");
	}

	/**
	 * Launch the simulation.
	 *
	 * @param path path of the scenario file.
	 */
	private  void launchSimulation(String path){
		HashMap<String,List<Object[]>> map = ProtocolTools.readProtocolJSON(path);
		List<Object[]> l_call = map.get("Calls");
		int num_node;
		for(Object[] func : l_call){
			num_node=(int)func[1];
			if(num_node==currentNode)
				process.getTimer().schedule(new ScheduledFunction((String)func[0],Arrays.copyOfRange(func,3,func.length),process,this),(long)func[2]);
			else
				send(new SchedMessage(currentNode,num_node,(String) func[0],(long)func[2],Arrays.copyOfRange(func,3,func.length)));
		}
		l_call=map.get("Off");
		for(Object[] func : l_call){
			num_node=(int)func[0];
			if(num_node==currentNode)
				process.getTimer().schedule(new ScheduledBreakDown(process),(long)func[1]);
			else
				send(new ShedBreakMessage(currentNode,num_node,(long)func[1]));
		}
		l_call=map.get("On");
		for(Object[] func : l_call){
			num_node=(int)func[0];
			if(num_node==currentNode)
				process.getTimer().schedule(new SchedDeploy(this),(long)func[1]);
			else
				send(new ShedOnMessage(currentNode,num_node,(long)func[1]));
		}
	}

}
