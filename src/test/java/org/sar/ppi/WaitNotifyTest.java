package org.sar.ppi;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sar.ppi.mpi.MpiRunner;
import org.sar.ppi.peersim.PeerSimRunner;

/**
 * ExampleNodeProces
 */
public class WaitNotifyTest extends NodeProcess {

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
	
	
	private final int N = 2;
	private int msgReceived = 0;
			

	
	public void affiche(){
		waiting( () -> msgReceived >=N );
		System.out.println("Noeud "+infra.getId()+": Bonjour !");
	}
	


	@MessageHandler
	public void processExampleMessage(ExampleMessage message) {
		int host = infra.getId();
		if(host==2) {
			affiche();
		}
		System.out.printf("Thread" + Thread.currentThread().getId() +" : %d Received '%s' from %d.\n", host, message.getS(), message.getIdsrc());
		msgReceived++;
		System.out.println("Noeud "+host+" Messages recus : "+msgReceived);
		if (host != 0) {
			int dest = (host + 1) % infra.size();
			infra.send(new ExampleMessage(infra.getId(), dest, "bonjour"));
			infra.send(new ExampleMessage(infra.getId(), dest, "bonjour"));
		}
		
		infra.exit();
		
	}

	@Override
	public void start() {
		
		if (infra.getId() == 0) {
			infra.send(new ExampleMessage(infra.getId(), 1, "bonjour"));
			infra.send(new ExampleMessage(infra.getId(), 1, "bonjour"));
			//new Thread (()->{affiche();}).start();
		}
	}

	//@Test
	public void MpiAnnotatedProcessTest() {
		String[] args = { WaitNotifyTest.class.getName(), MpiRunner.class.getName() };
		Ppi.main(args);
		assertTrue(true);
	}

	@Test
	public void PeersimWaitNotifyTest() {
		String[] args = { WaitNotifyTest.class.getName(), PeerSimRunner.class.getName() };
		Ppi.main(args);
		assertTrue(true);
	}
}
