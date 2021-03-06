package org.sar.ppi;

import static org.junit.Assert.assertTrue;

import org.junit.Assume;
import org.junit.Test;
import org.sar.ppi.events.Message;
import org.sar.ppi.mpi.MpiRunner;
import org.sar.ppi.peersim.PeerSimRunner;

public class NodeProcessTest extends NodeProcess {

	public static class ExampleMessage extends Message {
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

	@Override
	public void processMessage(Message message) {
		int host = infra.getId();
		System.out.println(
			"Thread" +
			Thread.currentThread().getId() +
			" " +
			host +
			" Received hello from " +
			message.getIdsrc()
		);
		if (host != 0) {
			int dest = (host + 1) % infra.size();
			infra.send(new ExampleMessage(infra.getId(), dest, "hello"));
		}
		infra.exit();
	}

	@Override
	public void init(String[] args) {
		if (infra.getId() == 0) {
			//System.err.println("SENDING FIRST MESSAGE");
			infra.send(new ExampleMessage(infra.getId(), 1, "hello"));
		}
	}

	@Test
	public void mpi() {
		Assume.assumeTrue(EnvUtils.mpirunExist());
		Ppi.main(this.getClass(), new MpiRunner());
		assertTrue(true);
	}

	@Test
	public void peersim() {
		Ppi.main(this.getClass(), new PeerSimRunner());
		assertTrue(true);
	}
}
