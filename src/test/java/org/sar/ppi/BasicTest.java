package org.sar.ppi;

import org.junit.Test;
import org.sar.ppi.mpi.MpiRunner;
import org.sar.ppi.peersim.PeerSimRunner;

import static org.junit.Assert.assertTrue;

public class BasicTest {

	@Test
	public void MpiExample() {
		Ppi.main(new String[] { ExampleNodeProcess.class.getName(), MpiRunner.class.getName() });
		assertTrue(true);
	}

	@Test
	public void PeersimExample() {
		Ppi.main(new String[] { ExampleNodeProcess.class.getName(), PeerSimRunner.class.getName() });
		assertTrue(true);
	}

	@Test
	public void MpiAnnotatedExample() {
		Ppi.main(new String[] { AnnotatedProcess.class.getName(), MpiRunner.class.getName() });
		assertTrue(true);
	}

	@Test
	public void PeersimAnnotatedExample() {
		Ppi.main(new String[] { AnnotatedProcess.class.getName(), PeerSimRunner.class.getName() });
		assertTrue(true);
	}

}
