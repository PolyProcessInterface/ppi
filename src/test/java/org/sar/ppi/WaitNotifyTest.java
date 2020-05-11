package org.sar.ppi;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

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
	private static int msgReceived = 0;
	private static int waiting = 0;
	private static List<Long> threads = new ArrayList<>();
			
	public void displayAfterNMessages() {
		System.out.println("## Thread "+Thread.currentThread().getId()+" : Going to wait ##");
		
		waiting++;
		
		threads.add(Thread.currentThread().getId());
		infra.waiting(msgReceived >= N);
		
		
		while(threads.get(0) != Thread.currentThread().getId()) {
			infra.waiting(false);
		}
		threads.remove(Thread.currentThread().getId());
		
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
		
		msgReceived++;
		if(msgReceived >= N && waiting > 0) {
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
		if((infra.getId()%2)==0) {
			// display something after N messages
			new Thread (()->{displayAfterNMessages();}).start(); 
		}
		if (infra.getId() == 0) {
			infra.send(new ExampleMessage(infra.getId(), 1, "bonjour"));
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
