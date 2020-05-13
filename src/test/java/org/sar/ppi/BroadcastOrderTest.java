package org.sar.ppi;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sar.ppi.peersim.PeerSimRunner;

import peersim.config.Configuration;

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
		System.out.printf("%d Received '%s' from %d\n", host%infra.size(), message.getS(), message.getIdsrc()%infra.size());
		infra.exit();
	}

	@Override
	public void start() {
		
		if ((infra.getId()%infra.size()) == 0) {
			

			for(int i=infra.getId()+1;i<infra.getId()+infra.size();i++) {

				infra.send(new ExampleMessage(infra.getId(),i, "OrderTest"));
			}
			infra.exit();
		}
	}

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;
	private static final Integer NETWORKSIZE = 10 ;
	private static final String PAR_SIZE = "network.size";
	
	@Before
	public void setUpStreams() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
	}

	@After
	public void restoreStreams() {
		System.setOut(originalOut);
		System.setErr(originalErr);
	}

	String outputPeersim;
	@Test
	public void PeersimBroadcastOrderTest() {
		String[] args = { BroadcastOrderTest.class.getName(), PeerSimRunner.class.getName(),NETWORKSIZE.toString()};
		Ppi.main(args);
		int networkSize=Configuration.getInt(PAR_SIZE);
		int i=0;
		
		outputPeersim=outContent.toString();
		Scanner scanner = new Scanner(outputPeersim);
		String[] expected=new String[networkSize];
		
		while (scanner.hasNextLine()) {
		  String line = scanner.nextLine();
		  if(line.isEmpty()) {	//Skipping empty output lines
			  continue;
		  }else {
			  String[] results=line.split(" ");
			  
			  if(i<networkSize-1) { // Initialisation des output expected pour chaque ligne lors du premier experiment
				  
				  expected[i]=results[0]+","+results[2]+","+results[4];
			  
			  }else{	// Comparaison des outputs pour chaque experiment au-delà du premier
				  
				  assertEquals(expected[i%(networkSize-1)],results[0]+","+results[2]+","+results[4]);
			  }
		  }
		i++;
		}
		scanner.close();

	}
}