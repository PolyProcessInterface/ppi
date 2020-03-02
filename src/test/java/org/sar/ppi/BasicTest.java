package org.sar.ppi;

import org.junit.Test;
import org.sar.ppi.mpi.MpiRunner;

import peersim.Simulator;

import static org.junit.Assert.assertTrue;

public class BasicTest {

	@Test
	public void runFirstBasicTest() {
		Ppi.main(new String[] { ExampleNodeProcess.class.getName(), MpiRunner.class.getName() });
	}

	@Test
	public void firstTestPeerSim() {

		String[] tab = new String[1];
		tab[0] = "notreconfig.conf";
		Simulator.main(tab);
		assertTrue(true);
	}

}
