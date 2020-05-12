package org.sar.ppi.peersim;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.util.ExtendedRandom;

public class PeerSimInit implements Control {

	private static final String PAR_PROTO="infrapid";
	private final int infrapid;
	
	/**
	* Configuration parameter used to initialize the random seed.
	* If it is not specified the current time is used.
	* @config
	*/
	public static final String PAR_SEED = "random.seed";
	
	public PeerSimInit(String prefix) {
		infrapid=Configuration.getPid(prefix+"."+PAR_PROTO);
	}

	//commandState.r=new ExtendedRandom(notreseed)
	@Override
	public boolean execute() {
		
		long seed =
				Configuration.getLong(PAR_SEED,System.currentTimeMillis());
		System.err.println("SETTING THE RANDOM.SEED TO "+seed);
		CommonState.r=new ExtendedRandom(seed);
		for(int i=0;i < Network.size();i++) {
			Node node=Network.get(i);
			PeerSimInfrastructure pInfra = (PeerSimInfrastructure) node.getProtocol(infrapid);
			pInfra.initialization(node);
		}
		return false;
	}
}