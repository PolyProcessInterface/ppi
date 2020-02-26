package org.sar.ppi.peersim;

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
	
	private final int my_pid; // identifiant du protocole
	
	public PeerSimInfrastructure(String prefix, NodeProcess np) {
		super(np);
		String tmp[]=prefix.split("\\.");
		my_pid=Configuration.lookupPid(tmp[tmp.length-1]);
		pid_transport=Configuration.getPid(prefix+"."+PAR_TRANSPORT);
	}
	
	/**
	 * Deep cloning necessary ! 
	 */
	public Object clone() {
		PeerSimInfrastructure psi= null;
		try {
			psi=(PeerSimInfrastructure) super.clone();
			psi.process=(NodeProcess) this.process.clone();
		}catch(CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return psi;
	}

	@Override
	public void run(String[] args) {
		String[] tab=new String[1];
		tab[0]="notreconfig";
		Simulator.main(tab);	// Direct dans le main ?
	}

	@Override
	public void send(Message message) {
		
		Node host=Network.get((int) message.getIdsrc());
		Transport tr= (Transport) host.getProtocol(pid_transport);
		tr.send(host,Network.get((int) message.getIddest()),message, my_pid);
	}


	@Override
	public void exit() {
		// TODO Auto-generated method stub

	}

	@Override
	public int size() {
		return Network.size();
	}

	public void initialisation(Node host) {
		if(host.getID()==0) {
			//sendFirstMessage(host); // this is just an example
		}
	}
	@Override
	public void processEvent(Node host, int pid, Object event) {
		if(pid!=my_pid) throw new IllegalArgumentException("Incoherence sur l'id de protocole");
		
		//TO DO -- AIGUILLAGE DU TRAITEMENT DE L'EVENT
		this.process.processMessage((Message) event);
	}

}
