package org.sar.ppi;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sar.ppi.mpi.MpiRunner;
import org.sar.ppi.peersim.PeerSimRunner;

public class NodeProcessTest extends NodeProcess {

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

	@Override
	public void processMessage(Message message) {
		int host = infra.getId();
		System.out.println("" + host + " Received hello from "  + message.getIdsrc());
		if (host != 0) {
			int dest = (host + 1) % infra.size();
			infra.send(new ExampleMessage(infra.getId(), dest, "hello"));
		}
		infra.exit();
	}

	@Override
	public void start() {
		if (infra.getId() == 0) {
			//System.err.println("SENDING FIRST MESSAGE");
			infra.send(new ExampleMessage(infra.getId(), 1, "hello"));
		}else {
			//System.err.println("NOT SENDING FIRST MESSAGE BECAUSE ID ==   "+infra.getId());
		}
	}

	@Test
	public void MpiExample() {
		Ppi.main(new String[] { NodeProcessTest.class.getName(), MpiRunner.class.getName() });
		assertTrue(true);
	}

	@Test
	public void PeersimExample() {
		Ppi.main(new String[] { NodeProcessTest.class.getName(), PeerSimRunner.class.getName() });
		assertTrue(true);
	}
}
