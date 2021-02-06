package org.sar.ppi.peersim;

import org.sar.ppi.Ppi;
import org.sar.ppi.events.ScheduledEvent;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.util.ExtendedRandom;

/**
 * PeerSimInit class.
 */
public class PeerSimInit implements Control {
	private static final String PAR_PROTO = "infrapid";
	public static final String PAR_SEED = "random.seed";
	private final int infraPid;

	/**
	 * Constructor for PeerSimInit.
	 *
	 * @param prefix a {@link java.lang.String} object.
	 */
	public PeerSimInit(String prefix) {
		infraPid = Configuration.getPid(prefix + "." + PAR_PROTO);
	}

	/** {@inheritDoc} */
	@Override
	public boolean execute() {
		long seed = Configuration.getLong(PAR_SEED, System.currentTimeMillis());
		boolean seedKeep = Configuration.contains(PAR_SEED + ".keep");
		if (seedKeep) {
			System.err.println("SETTING THE RANDOM.SEED TO " + seed);
			CommonState.r = new ExtendedRandom(seed);
		}
		for (int i = 0; i < Network.size(); i++) {
			Node node = Network.get(i);
			PeerSimInfrastructure pInfra = (PeerSimInfrastructure) node.getProtocol(infraPid);
			pInfra.initialize(node);
		}
		launchSimulation();
		return false;
	}

	private void launchSimulation() {
		for (ScheduledEvent e : Ppi.getConfig().getEvents()) {
			EDSimulator.add(e.getDelay(), e, Network.get(e.getNode()), infraPid);
		}
	}
}
