package org.sar.ppi;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sar.ppi.mpi.MpiRunner;
import org.sar.ppi.peersim.PeerSimRunner;

public class MutexTest extends NodeProcess {

	Integer father = 0;
	Integer next = null;
	boolean token = false;
	boolean requesting = false;
	int nbReq = 0;

	@Override
	public void start() {
		if (infra.getId() == father) {
			father = null;
			token = true;
		}
		try {
			while (nbReq < 5) {
				request();
				wait((MutexTest process) -> process.token == true);
				cs();
				release();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void request() {
		requesting = true;
		if (father != null) {
			infra.send(new Request(infra.getId(), father));
			father = null;
		}
		System.out.printf("%d entered critical section\n", infra.getId());
	}

	public void cs() {
	}

	public void release() {
		requesting = false;
		if (next != null) {
			infra.send(new Token(infra.getId(), next));
			token = false;
			next = null;
		}
		System.out.printf("%d left critical section\n", infra.getId());
	}

	@MessageHandler
	public void processRequest(Request request) {
		int host = infra.getId();
		System.out.printf("%d Received request from %d\n", host, request.getIdsrc());
		if (father == null) {
			if (requesting == true) {
				next = request.getIdsrc();
			} else {
				infra.send(new Token(host, request.getIdsrc()));
				token = false;
			}
		} else {
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

		public Request(int idsrc, int iddest) {
			super(idsrc, iddest);
		}
	}

	public static class Token extends Message {

		private static final long serialVersionUID = 1L;

		public Token(int idsrc, int iddest) {
			super(idsrc, iddest);
		}
	}

	@Test
	public void MpiAnnotatedProcessTest() {
		String[] args = { AnnotatedProcessTest.class.getName(), MpiRunner.class.getName() };
		Ppi.main(args);
		assertTrue(true);
	}

	@Test
	public void PeersimAnnotatedProcessTest() {
		String[] args = { AnnotatedProcessTest.class.getName(), PeerSimRunner.class.getName() };
		Ppi.main(args);
		assertTrue(true);
	}
}
