package org.sar.ppi.peersim;

import java.lang.reflect.InvocationTargetException;

import org.sar.ppi.*;
import peersim.*;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;
public class PeerSimInfrastructure extends Infrastructure implements EDProtocol {

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.process=np; // TO DO  à changer pour donner la classe précisée par PAR_NP dans le fichier de config
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
	public void run(String[] args) {
		String[] tab=new String[1];
		tab[0]="notreconfig.conf";
		Simulator.main(tab);	// Direct dans le main ?
	}

	@Override
	public void send(Message message) {

		if(running) {

			
			Node host = Network.get((int) message.getIdsrc());
			Transport tr = (Transport) host.getProtocol(pid_transport);
			Node dest = Network.get((int) message.getIddest());
			ObjectMessage obj = (ObjectMessage) message;
			System.err.println(Network.get((int) message.getIdsrc()) + " SENDING TO \n"+Network.get((int) message.getIddest()));
			tr.send(host, dest, obj.getContent(), my_pid);
			System.err.println("SENT!");
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

	public void initialization(Node host) {
		this.process.start();
	}

	@Override
	public void processEvent(Node host, int pid, Object event) {
		if(pid!=my_pid) throw new IllegalArgumentException("Inconsistency on protocol id");

		//TO DO -- AIGUILLAGE DU TRAITEMENT DE L'EVENT
		
		if(event instanceof Object) {

			System.err.println("ProcessEventinstanceof");
			process.processMessage((Message) event);
		}else {

			System.err.println("ProcessEvent");
			throw new IllegalArgumentException("Unknown event for this protocol");
		}
	}

}
