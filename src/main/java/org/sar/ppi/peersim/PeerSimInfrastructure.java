package org.sar.ppi.peersim;

import java.lang.reflect.InvocationTargetException;

import org.sar.ppi.Infrastructure;
import org.sar.ppi.NodeProcess;
import org.sar.ppi.Ppi;
import org.sar.ppi.communication.Message;
import org.sar.ppi.events.Event;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.NodeInitializer;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;


/**
 * PeerSimInfrastructure class.
 */
public class PeerSimInfrastructure extends Infrastructure implements EDProtocol , NodeInitializer {

	private static final String PAR_TRANSPORT="transport";

	private static String[] args;

	private final int transportPid; // id du protocole de transport

	private static final String PAR_NP="nodeprocess"; 

	private final int protocolPid; // identifiant du protocole

	private boolean running; // true while exit() hasnt been executed
	private static int cptID=0;

	/**
	 * Constructor for PeerSimInfrastructure.
	 *
	 * @param prefix a {@link java.lang.String} object.
	 */
	public PeerSimInfrastructure(String prefix) {
		super(null);
		NodeProcess np=null;
		try {
			np = (NodeProcess) Ppi.loader.loadClass(Configuration.getString(prefix+"."+PAR_NP)).getConstructor().newInstance();
			np.setInfra(this);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		this.process=np;
		String tmp[]=prefix.split("\\.");
		protocolPid=Configuration.lookupPid(tmp[tmp.length-1]);
		transportPid=Configuration.getPid(prefix+"."+PAR_TRANSPORT);
		running=true;
		currentNode=0;  
	}

	public static void setArgs(String[] pargs) {
		args = pargs;
	}

	/**
	 * Deep cloning necessary !
	 *
	 * @return a {@link java.lang.Object} object.
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

	/** {@inheritDoc} */
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return super.getId()%Network.size();
	}
	/** {@inheritDoc} */
	@Override
	public void send(Message message) {
		if(running) {

			Node nodeHost = Network.get(getId());
			Transport tr = (Transport) nodeHost.getProtocol(transportPid);
			Node nodeDest = Network.get(message.getIddest()%Network.size());
			//System.err.println(message.getIdsrc() + " SENDING TO "+message.getIddest());
			tr.send(nodeHost, nodeDest, message, protocolPid);
		}else {
			System.err.println("Not running : won't send the message from "+message.getIdsrc() +" to " +message.getIddest());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void exit() {
		running=false;
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return Network.size();
	}

	/** {@inheritDoc} */
	@Override
	public void initialize(Node node) {
		this.process.init(args.clone());
	}

	/** {@inheritDoc} */
	@Override
	public void processEvent(Node host, int pid, Object event) {
		if (pid != protocolPid) {
			throw new IllegalArgumentException("Inconsistency on protocol id");
		}
		if (event instanceof Event && isDeployed()){
			processEvent((Event) event);
		} else if (isDeployed()){
			throw new IllegalArgumentException("Unknown event for this protocol");
		}
	}



}
