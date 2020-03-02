package org.sar.ppi.peersim;

import org.sar.ppi.NodeProcess;
import org.sar.ppi.Runner;

import peersim.Simulator;

/**
 * PeerSimRunner
 */
public class PeerSimRunner implements Runner {

	@Override
	public void run(Class<? extends NodeProcess> processClass, String[] args) throws ReflectiveOperationException {
		// TODO use processClass to instantiate the right class in PeerSimInit
		String[] tab = new String[1];
		tab[0] = "notreconfig.conf";
		Simulator.main(tab);
	}
}