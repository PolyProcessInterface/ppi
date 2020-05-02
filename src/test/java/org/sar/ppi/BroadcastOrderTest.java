package org.sar.ppi;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sar.ppi.mpi.MpiRunner;
import org.sar.ppi.peersim.PeerSimRunner;

/**
 * ExampleNodeProces
 */
public class BroadcastOrderTest extends NodeProcess {

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
		infra.exit();
	}

	@Override
	public void start() {
		if (infra.getId() == 0) {
			for(int i=1;i<infra.size();i++) {
				infra.send(new ExampleMessage(0,i, "OrderTest"));
			}
		}
	}

	@Test
	public void MpiAnnotatedProcessTest() {
		String[] args = { BroadcastOrderTest.class.getName(), MpiRunner.class.getName() };
		Ppi.main(args);
		assertTrue(true);
	}

	@Test
	public void PeersimAnnotatedProcessTest() {
		String[] args = { BroadcastOrderTest.class.getName(), PeerSimRunner.class.getName() };
		Ppi.main(args);
		assertTrue(true);
	}
}