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
	private boolean sended = false;
	
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
		Node host = Network.get((int) message.getIdsrc());
		Transport tr = (Transport) host.getProtocol(pid_transport);
		Node dest = Network.get((int) message.getIddest());
		ObjectMessage obj = (ObjectMessage) message;
		tr.send(host, dest, obj.getContent(), my_pid);
		
		sended = true;
	}
	
	private void receive(Node host, Message message) {
		ObjectMessage obj = (ObjectMessage) message;
		String msg = obj.getContent().toString();
		System.out.println("Contenu : " + msg);
		if(!sended) {
			//this.send( Message a creer? ) ? ou process.processMessage((Message) event);
		}
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
	}

	@Override
	public int size() {
		return Network.size();
	}

	public void initialization(Node host) {
		if(host.getID()==0) {
			Message msg = new ObjectMessage(host.getID(), (host.getID()+1)%Network.size(), my_pid, "Test"); // envoi "Test" à son voisin
			this.send(msg);
			//sendFirstMessage(host); // this is just an example
		}
	}
	
	@Override
	public void processEvent(Node host, int pid, Object event) {
		if(pid!=my_pid) throw new IllegalArgumentException("Inconsistency on protocol id");
		
		//TO DO -- AIGUILLAGE DU TRAITEMENT DE L'EVENT (mais pr l'instant on en a qu'un)
		if(event instanceof Message) {
			this.receive(host, (Message) event);
			//process.processMessage((Message) event); // processMessage à implanter ici ou dans un NodeProtocol
		}else {
			throw new IllegalArgumentException("Unknown event for this protocol");
		}
		//this.process.processMessage((Message) event);
	}

}
