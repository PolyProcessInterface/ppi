package org.sar.ppi.mpi;

import org.sar.ppi.*;

import mpi.Comm;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;
import org.sar.ppi.simulator.ProtocolTools;


import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

/**
 * MpiInfrastructure
 */
public class MpiInfrastructure extends Infrastructure {

	protected boolean running = true;
	protected Comm comm;
	protected Timer timer = new Timer();
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
				Message msg = RetriveMessage(tab);
				if(msg instanceof  SchedMessage) {
					SchedMessage shed = (SchedMessage) msg;
					timer.schedule(new ScheduledFunction(shed.getName(),shed.getArgs(),process),shed.getDelay());
				}
				else
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
	public  void launchSimulation(String path){
		List<Object[]> l_call = ProtocolTools.readProtocolJSON(path);

		int num_node;
		for(Object[] func : l_call){
			num_node=(int)func[1];
			if(num_node==currentNode)
				timer.schedule(new ScheduledFunction((String)func[0],Arrays.copyOfRange(func,3,func.length),process),(long)func[2]);
			else
			send(new SchedMessage(currentNode,num_node,(String) func[0],(long)func[2],Arrays.copyOfRange(func,3,func.length)));

			System.out.println("je suis a la fin lanuch simulation");
		}
	}


    @Override
	public void exit() {
    	if(timer!=null)
    		timer.cancel();
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

}