package org.sar.ppi.mpi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sar.ppi.*;

import mpi.Comm;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;
import org.sar.ppi.communication.Message;
import org.sar.ppi.events.Event;
import org.sar.ppi.events.Scenario;
import org.sar.ppi.events.ScheduledEvent;

import java.io.*;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * MpiInfrastructure class.
 */
public class MpiInfrastructure extends Infrastructure {
	private static Logger logger = LogManager.getLogger();

	AtomicBoolean running = new AtomicBoolean(true);
	protected Comm comm;
	protected Queue<Message> sendQueue = new ConcurrentLinkedQueue<>();
	protected BlockingQueue<Message> recvQueue = new LinkedBlockingQueue<>();
	protected File scenario = null;
	/**
	 * Constructor for MpiInfrastructure.
	 *
	 * @param process a {@link org.sar.ppi.NodeProcess} object.
	 */
	public MpiInfrastructure(NodeProcess process, File scenario) {
		super(process);
		this.scenario = scenario;
	}


	/**
	 * Run the infrastructure.
	 *
	 * @param args an array of {@link java.lang.String} objects.
	 * @throws org.sar.ppi.PpiException if any.
	 */
	public void run(String[] args) throws PpiException {
		Thread executor = new Thread(new MpiProcess(process, this, args));
		try {
			MPI.InitThread(args, MPI.THREAD_FUNNELED);
			comm = MPI.COMM_WORLD;
			currentNode = comm.getRank();
			executor.start();
			if(scenario != null)
				get_my_tasks(scenario.getAbsolutePath());
			while (running.get() || !sendQueue.isEmpty()) {
				Status s = comm.iProbe(MPI.ANY_SOURCE, MPI.ANY_TAG);
				if (s != null) {
					recvMpi(s.getCount(MPI.BYTE), s.getSource(), s.getTag());
				}
				Message m = sendQueue.poll();
				if (m != null) {
					sendMpi(m);
				}
			}
			for (Thread t : threads.values()) {
				t.interrupt();
				logger.info("{} Interrupted waiting thread {}", getId(), t.getId());
				t.join();
				logger.info("{} Joined waiting thread {}", getId(), t.getId());
			}
			executor.interrupt();
			logger.info("{} Interrupted MpiProcess thread", getId());
			executor.join();
			logger.info("{} Joined MpiProcess thread", getId());
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
	protected void recvMpi(int size, int source, int tag) throws PpiException {
		try {
			byte [] tab = new byte[size];
			comm.recv(tab, size, MPI.BYTE, source, tag);
			Message msg = RetriveMessage(tab);
			if(!process.getIs_down())
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

	/** Make this method public for MpiInfrastructure */
	@Override
	public void processEvent(Event e) {
		super.processEvent(e);
	}

	private void get_my_tasks(String path){
		Scenario scenario;
		ObjectMapper mapper = new ObjectMapper();
		try {
			scenario = mapper.readValue(new File(path), Scenario.class);
		} catch (IOException e) {
			throw new PpiException("Invalid scenario file", e);
		}
		for (ScheduledEvent e : scenario.getEvents()) {
			if (e.getNode() != getId()) {
				continue;
			}
			process.getTimer().schedule(new EventTimer(this, e), e.getDelay());
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
}
