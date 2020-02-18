package org.sar.ppi;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import peersim.Simulator;

/**
 * Unit test for simple App.
 */
public class PeersimTest {
	/**
	 * Run Peersim.
	 */
	@Test
	public void runPeersim() {
		Simulator.main(new String[] { "message.conf" });
		assertTrue(true);
	}
}
