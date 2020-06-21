package org.sar.ppi;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assume;
import org.junit.Test;
import org.sar.ppi.communication.Message;
import org.sar.ppi.communication.MessageHandler;
import org.sar.ppi.mpi.MpiRunner;
import org.sar.ppi.peersim.PeerSimRunner;

public class BigObjectTest extends NodeProcess {

	public static class ExampleMessage extends Message {
		private static final long serialVersionUID = 1L;
		private List<Character> s;

		public ExampleMessage(int src, int dest, List<Character> s) {
			super(src, dest);
			this.s = s;
		}

		public List<Character> getS() {
			return s;
		}
	}

	@MessageHandler
	public void processExampleMessage(ExampleMessage message) {
		int host = infra.getId();
		System.out.printf("%d Received the message from %d\n", host, message.getIdsrc());
		if (host != 0) {
			int dest = (host + 1) % infra.size();
			infra.send(new ExampleMessage(infra.getId(), dest, newMsg()));
		}
		infra.exit();
	}

	public List<Character> newMsg() {
		int size = 10000;
		List<Character> msg = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			msg.add('a');
		}
		return msg;
	}

	@Override
	public void init(String[] args) {
		if (infra.getId() == 0) {
			infra.send(new ExampleMessage(infra.getId(), 1, newMsg()));
		}
	}

	@Test
	public void mpi() {
		Assume.assumeTrue(EnvUtils.mpirunExist());
		Ppi.main(this.getClass(), new MpiRunner(), new String[0], 3);
		assertTrue(true);
	}

	@Test
	public void peersim() {
		Ppi.main(this.getClass(), new PeerSimRunner(), new String[0], 3);
		assertTrue(true);
	}
}
