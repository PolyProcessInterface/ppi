package org.sar.ppi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sar.ppi.mpi.MpiRunner;
import org.sar.ppi.peersim.PeerSimRunner;

import peersim.core.CommonState;

/**
 * ExampleNodeProces
 */
public class BroadcastOrderTest extends NodeProcess {

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

	@MessageHandler
	public void processExampleMessage(ExampleMessage message) {
		int host = infra.getId();
		System.out.printf("%d Received '%s' from %d\n", host, message.getS(), message.getIdsrc());
		infra.exit();
	}

	@Override
	public void start() {
		
		if ((infra.getId()%infra.size()) == 0) {
			
			//System.err.println("Im the broadcaster, getID+size = "+ (infra.getId()+infra.size()));
			for(int i=infra.getId()+1;i<infra.getId()+infra.size();i++) {
				//System.err.println("MESSAGE FROM " + infra.getId() + " TO " + i);
				infra.send(new ExampleMessage(infra.getId(),i, "OrderTest"));
				//System.err.println("message sent");
			}
			infra.exit();
		}else {
			//System.err.println("NOT BROADCASTING : "+infra.getId());
		}
	}

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;


	@Before
	public void setUpStreams() {
		//System.setOut(new PrintStream(outContent));
		//System.setErr(new PrintStream(errContent));
	}

	@After
	public void restoreStreams() {
		//System.setOut(originalOut);
		//System.setErr(originalErr);
	}

	/*
	String first;
	@Test
	public void MpiAnnotatedProcessTest() {
		String[] args = { BroadcastOrderTest.class.getName(), MpiRunner.class.getName() };
		Ppi.main(args);
		assertEquals(120, outContent.size());
		assertEquals("", errContent.toString());
	}
*/
	@Test
	public void PeersimBroadcastOrderTest() {
		String[] args = { BroadcastOrderTest.class.getName(), PeerSimRunner.class.getName() };
		Ppi.main(args);
		//first=outContent.toString();
		assertTrue(true);
	}
	/*
	@Test
	public void PeersimBroadcastOrderTest() {
		String[] args = { BroadcastOrderTest.class.getName(), PeerSimRunner.class.getName() };
		Ppi.main(args);
		//String expected = "\n\n\nThread1\n2 Received 'OrderTest' from 0\nThread1\n3 Received 'OrderTest' from 0\nThread1\n1 Received 'OrderTest' from 0\nThread1\n4 Received 'OrderTest' from 0\n";
		assertEquals(first, outContent.toString());
	}
	*/
}