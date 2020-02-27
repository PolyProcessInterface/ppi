package org.sar.ppi.peersim;

import org.sar.ppi.NodeProcess;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class PeerSimInit implements Control {

	private static final String PAR_PROTO="infrapid";
	private final int infrapid;
	
	public PeerSimInit(String prefix) {
		infrapid=Configuration.getPid(prefix+"."+PAR_PROTO);
	}

	@Override
	public boolean execute() {
		
		for(int i=0;i < Network.size();i++) {
			Node node=Network.get(i);
			
			PeerSimInfrastructure pInfra = (PeerSimInfrastructure) node.getProtocol(infrapid);
			pInfra.initialization(node);
			//NodeProcess np= (NodeProcess) node.getProtocol(infrapid);
			//np.infra
			//prot.initialisation(node);
			//prot.startNode((org.sar.ppi.Node) node);
		}
		return false;
	}

}