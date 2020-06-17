package org.sar.ppi;

import org.junit.Assume;
import org.junit.Test;
import org.sar.ppi.communication.Message;
import org.sar.ppi.communication.MessageHandler;
import org.sar.ppi.mpi.MpiRunner;
import org.sar.ppi.peersim.PeerSimRunner;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * Hello ring started with a scenario
 */
public class HelloRingScenarioTest extends NodeProcess{
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
    public void ExempleMsg(NodeBreakDownTest.ExampleMessage message) {
        int host = infra.getId();
        System.out.printf("%d Received '%s' from %d\n", host, message.getS(), message.getIdsrc());
        int dest = (host + 1) % infra.size();
        infra.send(new NodeBreakDownTest.ExampleMessage(infra.getId(), dest, "Hello"));
        infra.exit();
        System.out.printf("%d : has finished \n",infra.getId());
    }

    public void FirstToSend(){
        System.out.printf("%d : First to send well be \n",infra.getId());
        infra.send(new NodeBreakDownTest.ExampleMessage(infra.getId(),(infra.getId() + 1) % infra.size(), "Hello"));
    }
    public void end(){
        infra.exit();
        System.out.printf("%d : has finished \n",infra.getId());
    }


    @Override
    public void init(String[] args) {
        System.out.printf("%d: Nothing to do \n",infra.getId());
    }

    @Test
    public void MpiHelloRingScenarioTest() {
        Assume.assumeTrue(Environment.mpirunExist());
        Ppi.main(this.getClass(), new MpiRunner(), new String[0], 3, new File("src/test/resources/HelloRingScenarioTest.json"));
        assertTrue(true);
        System.out.println("Teste BreakDown for Mpi ok");
    }

    @Test
    public void PeersimHelloRingScenarioTest() {
        Ppi.main(this.getClass(), new PeerSimRunner(), new String[0], 3, new File("src/test/resources/HelloRingScenarioTest.json"));
        assertTrue(true);
        System.out.println("Teste BreakDown for Peersim ok");
    }
}
