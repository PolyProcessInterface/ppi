package org.sar.ppi;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sar.ppi.communication.Message;
import org.sar.ppi.communication.MessageHandler;
import org.sar.ppi.mpi.MpiRunner;

import static org.junit.Assert.assertTrue;

import org.sar.ppi.peersim.PeerSimRunner;
import org.sar.ppi.tools.ProtocolTools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Example on off process
 */
public class NodeBreakDownTest extends NodeProcess {
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

    public void End() {
        System.out.println("End of sequence");
        infra.exit();
    }

    @Override
    public void init(String[] args) {
        if (infra.getId() == 0) {
            infra.send(new ExampleMessage(infra.getId(), 1, "Hello"));
            infra.exit();
        }else
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    @Test
    public void MpitesteProtocolBreakDown() {
        Assume.assumeTrue(Environment.mpirunExist());
        Ppi.main(this.getClass(), new MpiRunner(), new String[0], 3, new File(fileName));
        assertTrue(true);
        System.out.println("Teste BreakDown for Mpi ok");
    }

    @Test
    public void PeersimtesteProtocolBreakDown() {
        Ppi.main(this.getClass(), new PeerSimRunner(), new String[0], 3, new File(fileName));
        assertTrue(true);
        System.out.println("Teste BreakDown for Peersim ok");
    }

    @BeforeClass
    @SuppressWarnings("unchecked")
    public static void createJsonTeste() {
        try {
            FileWriter filew = new FileWriter(fileName);
            JSONObject toWrite = new JSONObject();
            JSONArray array = new JSONArray();
            //off
            long delay =1;
            int node = 0;
            JSONObject B1 = ProtocolTools.StateBuilder(node, delay);
            array.add(B1);

            toWrite.put("Off", array);
            array = new JSONArray();
            delay = 500;
            array = new JSONArray();
            List<Object> objects = new ArrayList<>();
           array.add(ProtocolTools.eventBuilder("End", 0, 5000, objects));
            toWrite.put("events", array);

            filew.write(toWrite.toString());
            filew.flush();
            filew.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void  after(){
        try {
            Files.deleteIfExists(Paths.get(fileName));
            System.out.println("End node down Test");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
