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
	
	private final int N = 10;
	private static int msgSended = 0;
	private static int waiting = 0;
			
	public void displayAfterNMessages() {
		System.out.println("## Thread "+Thread.currentThread().getId()+" : Going to wait ##");
		
		waiting++;
		infra.waiting(msgSended >= N);
		
		System.out.println("## Thread "+Thread.currentThread().getId()+" : No more waiting ##");
		
		waiting--;
		if(waiting>0) infra.notifyingAll();

		if(waiting==0) {
			synchronized (lock) {
				lock.notifyAll();
			}
		}
	}

	@MessageHandler
	public void processExampleMessage(ExampleMessage message) {
		int host = infra.getId();
		System.out.printf("Thread" + Thread.currentThread().getId() +" : %d Received '%s' from %d.\n", host, message.getS(), message.getIdsrc());
		if (host != 0) {
			int dest = (host + 1) % infra.size();
			infra.send(new ExampleMessage(infra.getId(), dest, "bonjour"));
		}
		
		msgSended++;
		if(msgSended >= N && waiting > 0) {
			synchronized (lock) {
				infra.notifyingAll();
				try {
					lock.wait(); // wait until displayed
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		infra.exit();
	}

	@Override
	public void start() {
		if (infra.getId() == 0) {
			infra.send(new ExampleMessage(infra.getId(), 1, "bonjour"));
		}
		
		if((infra.getId()%2)==0) {
			// display something after N messages
			new Thread (()->{displayAfterNMessages();}).start(); 
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
