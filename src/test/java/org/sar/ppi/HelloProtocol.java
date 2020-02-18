package org.sar.ppi;

import java.util.ArrayList;
import java.util.List;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;

public class HelloProtocol implements EDProtocol {

	private static final String PAR_TRANSPORT="transport";
	private static final String PAR_MAXSIZELIST = "maxsizelist";
	
	private final int pid_transport;
	private final int maxsizelist;
	
	private final int my_pid; // identifiant du protocole
	private List<Integer> myList; // liste propre à chaque noeud
	private boolean deja_dit_bonjour; // indique si message deja envoye
	
	public HelloProtocol(String prefix) {
		
		String tmp[]=prefix.split("\\.");
		my_pid=Configuration.lookupPid(tmp[tmp.length-1]);
		pid_transport=Configuration.getPid(prefix+"."+PAR_TRANSPORT);
		maxsizelist=Configuration.getInt(prefix+"."+PAR_MAXSIZELIST);
		myList=new ArrayList<>();
		deja_dit_bonjour=false;
	}

	public Object clone() {
		HelloProtocol ap= null;
		try {
			ap=(HelloProtocol) super.clone();
			ap.myList=new ArrayList<>();
		}catch(CloneNotSupportedException e) {} // NEVER HAPPENS
		return ap;
	}
	
	//Un noeud souhaite faire sa diffusion du message Hello
	public void direBonjour(Node host) {
		Transport tr= (Transport) host.getProtocol(pid_transport);
		Node dest=Network.get((int) ((host.getID()+1)%Network.size()));
		Message mess= new HelloMessage(host.getID(),(host.getID()+1)%Network.size(),my_pid, new ArrayList<>(myList));
		tr.send(host, dest, mess, my_pid);

		deja_dit_bonjour=true;
	}

	//traitement lorsqu'on recoit un HelloMessage
	private void receiveHelloMessage(Node host, HelloMessage mess) {
		System.out.println("Noeud "+ host.getID() + " : a reçu Hello de "+mess.getIdsrc()+ " sa liste = "+mess.getInfo());
		if(!deja_dit_bonjour) {
			direBonjour(host);
		}
	}
	
	//init la liste aleatoire et permet au node zero d'init la premiere diffusion
	public void initialisation(Node host) {
		int size_list=CommonState.r.nextInt(maxsizelist);
		for(int i=0; i< size_list;i++) {
			myList.add(CommonState.r.nextInt(128));
		}
		if(host.getID()==0) {
			direBonjour(host);
		}
	}

	@Override
	public void processEvent(Node host, int pid, Object event) {
		if(pid!=my_pid) throw new IllegalArgumentException("Incoherence sur l'id de protocole");
		if(event instanceof HelloMessage) {
			receiveHelloMessage(host,(HelloMessage) event);
		}else {
			throw new IllegalArgumentException("Evenement inconnu pour ce protocole");
		}
	}

}
