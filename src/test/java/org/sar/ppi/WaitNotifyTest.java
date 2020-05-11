package org.sar.ppi;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
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
	
	private final int N = 3;
	private static int msgSended = 0;
	private static int cpt = 0;
	
	public void display() {
		System.out.println("\n###########################");
		System.out.println("Thread "+Thread.currentThread().getId()+" : Going to wait");
		System.out.println("###########################\n");
		cpt++;
		infra.waiting(msgSended >= N);
		
		System.out.println("\n###########################");
		System.out.println("Thread "+Thread.currentThread().getId()+" : No more waiting");
		System.out.println("###########################\n");
		/*cpt--;
		if(cpt>0) infra.notifyingAll();*/
	}

	@MessageHandler
	public void processExampleMessage(ExampleMessage message) {
		int host = infra.getId();
		System.out.printf("Thread " + Thread.currentThread().getId() +" %d Received '%s' from %d\n", host, message.getS(), message.getIdsrc());
		if (host != 0) {
			int dest = (host + 1) % infra.size();
			infra.send(new ExampleMessage(infra.getId(), dest, "bonjour"));
		}
		
		msgSended++;
		if(msgSended >= N) {
			infra.notifyingAll();
		}
		
		infra.exit();
	}

	@Override
	public void start() {
		if (infra.getId() == 0) {
			infra.send(new ExampleMessage(infra.getId(), 1, "bonjour"));
			//new Thread (()->{display();}).start();
		}
		if((infra.getId()%2)==0) {
			new Thread (()->{display();}).start();
		}
	}

	/*
	@Test
	public void MpiAnnotatedProcessTest() {
		String[] args = { WaitNotifyTest.class.getName(), MpiRunner.class.getName() };
		Ppi.main(args);
		assertTrue(true);
	}
	*/

	@Test
	public void PeersimAnnotatedProcessTest() {
		String[] args = { WaitNotifyTest.class.getName(), PeerSimRunner.class.getName() };
		Ppi.main(args);
		assertTrue(true);
	}
}
