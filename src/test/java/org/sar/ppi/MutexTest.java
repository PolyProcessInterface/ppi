package org.sar.ppi;

import static org.junit.Assert.assertTrue;

import org.junit.Assume;
import org.junit.Test;
import org.sar.ppi.communication.Message;
import org.sar.ppi.communication.MessageHandler;
import org.sar.ppi.mpi.MpiRunner;
import org.sar.ppi.peersim.PeerSimRunner;

public class MutexTest extends NodeProcess {

	Integer father = 0;
	Integer next = null;
	boolean token = false;
	boolean requesting = false;
	int nbCS = 0;

	// terminaison
	int nbEnd = 0;

	@Override
	public void start() {
		if (infra.getId() == father) {
			father = null;
			token = true;
		}
	}

	public void doSomething() {
		try {
			infra.wait(() -> requesting == false);
			request();
			infra.wait(() -> token == true);
			cs();
			release();

			// terminaison
			if (nbCS == 2) {
				System.out.printf("%d Had 2 critical sections\n", infra.getId());
				infra.send(new End(infra.getId(), 0));
			}
		} catch (InterruptedException e) {
			System.out.printf("%d Was interrupted while waiting\n", infra.getId());
		}
	}

	public void request() {
		requesting = true;
		if (father != null) {
			infra.send(new Request(infra.getId(), father));
			father = null;
		}
		System.out.printf("%d Requested critical section\n", infra.getId());
	}

	public void cs() {
		System.out.printf("%d Entered critical section\n", infra.getId());
		nbCS++;
	}

	public void release() {
		requesting = false;
		System.out.printf("%d left critical section\n", infra.getId());
		if (next != null) {
			System.out.printf("%d Send token to %d\n", infra.getId(), next);
			infra.send(new Token(infra.getId(), next));
			token = false;
			next = null;
		}
	}

	@MessageHandler
	public void processRequest(Request request) {
		int host = infra.getId();
		System.out.printf("%d Received request from %d\n", host, request.getIdsrc());
		if (father == null) {
			if (requesting == true) {
				System.out.printf("%d Set %d as next\n", host, request.getIdsrc());
				next = request.getIdsrc();
			} else {
				System.out.printf("%d Send token to %d\n", host, request.getIdsrc());
				infra.send(new Token(host, request.getIdsrc()));
				token = false;
			}
		} else {
			System.out.printf("%d Pass request to %d\n", host, father);
			infra.send(new Request(request.getIdsrc(), father));
		}
		father = request.getIdsrc();
	}

	@MessageHandler
	public void processToken(Token t) {
		int host = infra.getId();
		System.out.printf("%d Received token from %d\n", host, t.getIdsrc());
		token = true;
	}


	public static class Request extends Message {
		private static final long serialVersionUID = 1L;
		public Request(int idsrc, int iddest) { super(idsrc, iddest); }
	}

	public static class Token extends Message {
		private static final long serialVersionUID = 1L;
		public Token(int idsrc, int iddest) { super(idsrc, iddest); }
	}

	// terminaison
	@MessageHandler
	public void processEnd(End end) {
		if (infra.getId() == 0) {
			nbEnd++;
			if (nbEnd == 5) {
				for (int i = 1; i < 6; i++) {
					infra.send(new End(0, i));
				}
				System.out.printf("%d Called exit\n", infra.getId());
				infra.exit();
			}
		} else {
			System.out.printf("%d Called exit\n", infra.getId());
			infra.exit();
		}
	}
	public static class End extends Message {
		private static final long serialVersionUID = 1L;
		public End(int idsrc, int iddest) { super(idsrc, iddest); }
	}

	@Test
	public void MpiMutexTest() {
		Assume.assumeTrue(Environment.mpirunExist());
		String[] args = { this.getClass().getName(), MpiRunner.class.getName(), "6", "src/test/resources/MutexTest.json" };
		Ppi.main(args);
		assertTrue(true);
	}

	@Test
	public void PeersimMutexTest() {
		String[] args = { this.getClass().getName(), PeerSimRunner.class.getName(), "6", "src/test/resources/MutexTest.json" };
		Ppi.main(args);
		assertTrue(true);
	}
}
