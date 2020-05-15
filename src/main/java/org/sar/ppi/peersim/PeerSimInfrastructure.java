package org.sar.ppi.peersim;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.sar.ppi.*;
import org.sar.ppi.simulator.peersim.SchedEvent;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.NodeInitializer;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;


public class PeerSimInfrastructure extends Infrastructure implements EDProtocol , NodeInitializer {

	private static final String PAR_TRANSPORT="transport";

	private final int pid_transport; // id du protocole de transport

	private static final String PAR_NP="nodeprocess"; 

	private final int my_pid; // identifiant du protocole

	private boolean running; // true while exit() hasnt been executed
	private static int cptID=0;


	public PeerSimInfrastructure(String prefix) {
		super(null);
		NodeProcess np=null;
		try {
			np = (NodeProcess) Class.forName(Configuration.getString(prefix+"."+PAR_NP)).getConstructor().newInstance();
			np.setInfra(this);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		this.process=np;
		String tmp[]=prefix.split("\\.");
		my_pid=Configuration.lookupPid(tmp[tmp.length-1]);
		pid_transport=Configuration.getPid(prefix+"."+PAR_TRANSPORT);
		running=true;
		currentNode=0;  
	}

	/**
	 * Deep cloning necessary ! 
	 */
	public Object clone() {
		PeerSimInfrastructure psi= null;
		try {
			psi=(PeerSimInfrastructure) super.clone();
			psi.process=(NodeProcess) this.process.clone();
			psi.process.setInfra(psi);
			psi.running=true;
			psi.currentNode=cptID++;
		}catch(CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return psi;
	}

	@Override
	public void send(Message message) {
		if(running) {
			Node nodeHost = Network.get(this.getId());
			Transport tr = (Transport) nodeHost.getProtocol(pid_transport);
			Node nodeDest = Network.get(message.getIddest());
			//System.err.println(message.getIdsrc() + " SENDING TO "+message.getIddest());
			tr.send(nodeHost, nodeDest, message, my_pid);
		}
	}

	@Override
	public void exit() {
		running=false;
	}

	@Override
	public int size() {
		return Network.size();
	}

	@Override
	public void initialize(Node node) {
		this.process.start();
	}

	@Override
	public void processEvent(Node host, int pid, Object event) {
		if(pid!=my_pid) throw new IllegalArgumentException("Inconsistency on protocol id");
		//a mettre dans process message ici juste pour les teste
		if(event instanceof SchedEvent){
			SchedEvent shed = (SchedEvent) event;
			String name = shed.getFuncName();
			for(Method m : process.getClass().getMethods()){
				if(m.getName().equals(name))
					try {
						m.invoke(process,shed.getArgs());
					} catch (IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
			}
			return;
		}

		if (event instanceof Message) {
			System.out.println("Thread" + Thread.currentThread().getId()+" : main thread : ");
			process.processMessage((Message) event);
			
		} else {
			throw new IllegalArgumentException("Unknown event for this protocol");
		}
	}

}
