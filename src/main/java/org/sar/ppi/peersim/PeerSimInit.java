package org.sar.ppi.peersim;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.util.ExtendedRandom;
import peersim.dynamics.NodeInitializer;


public class PeerSimInit implements Control {

	private static final String PAR_PROTO="infrapid";
	public static final String PAR_SEED = "random.seed";
	private final int infrapid;
	
	
	public PeerSimInit(String prefix) {
		infrapid=Configuration.getPid(prefix+"."+PAR_PROTO);
	}

	@Override
	public boolean execute() {
		
		long seed =	Configuration.getLong(PAR_SEED,System.currentTimeMillis());
		System.err.println("SETTING THE RANDOM.SEED TO "+seed);
		CommonState.r=new ExtendedRandom(seed);
		
		for(int i=0;i < Network.size();i++) {
			Node node=Network.get(i);
			PeerSimInfrastructure pInfra = (PeerSimInfrastructure) node.getProtocol(infrapid);
			pInfra.initialize(node);
		}
		return false;
	}
}