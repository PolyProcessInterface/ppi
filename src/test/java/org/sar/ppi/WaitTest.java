package org.sar.ppi;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sar.ppi.mpi.MpiRunner;
import org.sar.ppi.peersim.PeerSimRunner;

/**
 * ExampleWait
 */
public class WaitTest extends NodeProcess {

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
			
	public void hello(){
		try {
			infra.wait( () -> msgReceived >=N );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(infra.getId()+" Hello !");
	}
	


	@MessageHandler
	public void processExampleMessage(ExampleMessage message) {
		int host = infra.getId();
		System.out.printf("%d Received '%s' from %d.\n", host, message.getS(), message.getIdsrc());
		msgReceived++;
		int dest = (host + 1) % infra.size();
		if (host != 0 && msgReceived == 1) {
		    infra.send(new ExampleMessage(infra.getId(), dest, "hello"));
		    if(host==2) hello();
		} else {
			infra.send(new ExampleMessage(infra.getId(), dest, "hello"));
		    infra.exit();
		}
		
	}

	@Override
	public void start() {
		
		if (infra.getId() == 0) {
			infra.send(new ExampleMessage(infra.getId(), 1, "hello"));
		}
	}

	@Test
	public void MpiAnnotatedProcessTest() {
		String[] args = { WaitTest.class.getName(), MpiRunner.class.getName() };
		Ppi.main(args);
		assertTrue(true);
	}

	@Test
	public void PeersimWaitNotifyTest() {
		String[] args = { WaitTest.class.getName(), PeerSimRunner.class.getName() };
		Ppi.main(args);
		assertTrue(true);
	}
}
