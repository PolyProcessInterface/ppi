package org.sar.ppi;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sar.ppi.mpi.MpiRunner;
import org.sar.ppi.peersim.PeerSimRunner;

/**
 * ExampleNodeProces
 */
public class AnnotatedProcessTest extends NodeProcess {

	public static class ExampleMessage extends Message{
	
		private static final long serialVersionUID = 1L;
		private String s;
		public ExampleMessage(int src, int dest, String s) {
			super(src, dest);
			this.s = s;
		}

		public String getS() {
			return s;
		}

	}

	@MessageHandler
	public void processExampleMessage(ExampleMessage message) {
		int host = infra.getId();
		System.out.printf("%d Received '%s' from %d\n", host, message.getS(), message.getIdsrc());
		if (host != 0) {
			int dest = (host + 1) % infra.size();
			infra.send(new ExampleMessage(infra.getId(), dest, "bonjour"));
		}
		infra.exit();
	}

	@Override
	public void start() {
		if (infra.getId() == 0) {
			infra.send(new ExampleMessage(infra.getId(), 1, "bonjour"));
		}
	}

	@Test
	public void MpiAnnotatedProcessTest() {
		Ppi.main(new String[] { AnnotatedProcessTest.class.getName(), MpiRunner.class.getName() });
		assertTrue(true);
	}

	@Test
	public void PeersimAnnotatedProcessTest() {
		Ppi.main(new String[] { AnnotatedProcessTest.class.getName(), PeerSimRunner.class.getName() });
		assertTrue(true);
	}
}
