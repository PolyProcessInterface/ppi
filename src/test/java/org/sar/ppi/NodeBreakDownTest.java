package org.sar.ppi;


import org.junit.Assume;
import org.junit.Test;
import org.sar.ppi.communication.Message;
import org.sar.ppi.communication.MessageHandler;
import org.sar.ppi.mpi.MpiRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.sar.ppi.peersim.PeerSimRunner;

import java.io.EOFException;
import java.io.File;

/**
 * Example on off process
 */
public class NodeBreakDownTest extends RedirectedTest {
    static String fileName = System.getProperty("user.dir") + "/testeJson.json";

    public static class ExampleMessage extends Message {

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
    public void ExempleMsg(ExampleMessage message) {
        int host = infra.getId();
        System.out.printf("%d Received '%s' from %d\n", host, message.getS(), message.getIdsrc());
        if (host != 0) {
            int dest = (host + 1) % infra.size();
            infra.send(new ExampleMessage(infra.getId(), dest, "Hello"));
            infra.exit();
        }else {
            System.out.println(" not the right path");
        }
    }

    public void end() {
        System.out.println("End of sequence");
        infra.exit();
    }

    @Override
    public void init(String[] args) {
        if (infra.getId() == 0) {
            infra.send(new ExampleMessage(infra.getId(), 1, "Hello"));
        }
    }

    @Test
	public void MpitesteProtocolBreakDown() throws EOFException {
        Assume.assumeTrue(Environment.mpirunExist());
        Ppi.main(this.getClass(), new MpiRunner(), new String[0], 3, new File("src/test/resources/NodeBreakDownTest.json"));
		String line;
		line = nextNonEmpty();
		assertEquals("1 Received 'Hello' from 0", line);
		line = nextNonEmpty();
		assertEquals("2 Received 'Hello' from 1", line);
		assertTrue(!hasNextNonEmpty());
        System.out.println("Teste BreakDown for Mpi ok");
    }

    @Test
	public void PeersimtesteProtocolBreakDown() throws EOFException {
        Ppi.main(this.getClass(), new PeerSimRunner(), new String[0], 3, new File("src/test/resources/NodeBreakDownTest.json"));
		String line;
		for (int i = 0; i < 2; i++) {
			line = nextNonEmpty();
			assertEquals("1 Received 'Hello' from 0", line);
			line = nextNonEmpty();
			assertEquals("2 Received 'Hello' from 1", line);
		}
		assertTrue(!hasNextNonEmpty());
        System.out.println("Teste BreakDown for Peersim ok");
    }
}
