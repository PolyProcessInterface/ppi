package org.sar.ppi;

import static org.junit.Assert.assertTrue;

import org.junit.Assume;
import org.junit.Test;
import org.sar.ppi.communication.Message;
import org.sar.ppi.communication.MessageHandler;
import org.sar.ppi.mpi.MpiRunner;
import org.sar.ppi.peersim.PeerSimRunner;

public class RingExample extends NodeProcess {

	public static class RingMessage extends Message {
	
		private static final long serialVersionUID = 1L;
		private int content;
		public RingMessage(int src, int dest, int c) {
			super(src, dest);
			this.content = c;
		}

		public int getContent() {
			return content;
		}

	}

	@MessageHandler
	public void processRingMessage(RingMessage message) {
		int host = infra.getId();
		System.out.println("Noeud "+ host + " a reçu " + message.getContent() + " de " + message.getIdsrc());
		if (host != 0) {
			int dest = (host + 1) % infra.size();
			infra.send(new RingMessage(infra.getId(), dest, message.getContent()+1));
		}
		infra.exit();
	}

	@Override
	public void init(String[] args) {
		if (infra.getId() == 0) {
			infra.send(new RingMessage(infra.getId(), 1, 0));
		}
	}

	@Test
	public void MpiExample() {
		Assume.assumeTrue(Environment.mpirunExist());
		Ppi.main(this.getClass(), new MpiRunner());
		assertTrue(true);
	}

	@Test
	public void PeersimExample() {
		Ppi.main(this.getClass(), new PeerSimRunner());
		assertTrue(true);
	}
}
