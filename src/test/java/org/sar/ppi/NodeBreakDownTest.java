package org.sar.ppi;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    public void processExampleMessage(ExampleMessage message) {
        int host = infra.getId();
        if(message.getS().equals("exit")){
            infra.exit();
            return;
        }
        System.out.printf("%d Received '%s' from %d\n", host, message.getS(), message.getIdsrc());
        if (host != 0) {
            int dest = (host + 1) % infra.size();
            send_msg(dest, "Bonjour");
        }else{
            System.err.println(" je ne suis pas senser passer par la ");
        }
        infra.exit();
    }




    public void send_msg(int dest, String msg) {
        infra.send(new ExampleMessage(infra.getId(), dest, msg));
    }

    public void End() {
        System.out.println("Fin de l'execution");
        processExampleMessage(new ExampleMessage(infra.getId(),infra.getId(),"exit"));
    }

    @Override
    public void init(String[] args) {
        if (infra.getId() == 0) {
            infra.send(new ExampleMessage(infra.getId(), 1, "bonjour"));
            infra.exit();
        }
    }

    @Test
    public void MpitesteProtocolBreakDown() {
        Assume.assumeTrue(Environment.mpirunExist());
        Ppi.main(this.getClass(), new MpiRunner(), new String[0], 3, new File(fileName));
        assertTrue(true);
    }

    @Test
    public void PeersimtesteProtocolBreakDown() {
        Ppi.main(this.getClass(), new PeerSimRunner(), new String[0], 3, new File(fileName));
        assertTrue(true);
        System.out.println("Teste BreakDown from Json Peersim ok");
    }

    @BeforeClass
    @SuppressWarnings("unchecked")
    public static void createJsonTeste() {
        try {
            FileWriter filew = new FileWriter(fileName);
            JSONObject toWrite = new JSONObject();
            JSONArray array = new JSONArray();
            //off
            long delay =100;
            int node = 0;
            JSONObject B1 = ProtocolTools.StateBuilder(node, delay);
            array.add(B1);

            toWrite.put("Off", array);

            array = new JSONArray();
            //on
            delay = 500;
            array.add(ProtocolTools.StateBuilder(node, 4 * delay));
            // toWrite.put("On", array);


            delay = 500;
            array = new JSONArray();
            List<Object> objects = new ArrayList<>();
            array.add(ProtocolTools.eventBuilder("End", 1, 900, objects));
            array.add(ProtocolTools.eventBuilder("End", 2, 900, objects));
            toWrite.put("events", array);

            filew.write(toWrite.toString());
            filew.flush();
            filew.close();
            FileReader fileread = new FileReader(fileName);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(fileread);


            //Test
            List<Object[]> arrayTuple = ProtocolTools.BreakDownFromJSON(jsonObject, "Off");
            /*assertEquals(arrayTuple.size(),2);
            assertEquals(Integer.parseInt(arrayTuple.get(0)[0].toString()),1);
            assertEquals(Integer.parseInt(arrayTuple.get(1)[0].toString()),2);
            assertEquals(arrayTuple.get(1)[1],delay);*/
            fileread.close();
        } catch (IOException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void  after(){
        /*try {
            Files.deleteIfExists(Paths.get(fileName));
            System.out.println("End node down Test");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}





/*public class NodeBreakDownTest extends NodeProcess{

    public class ExampleMessage extends Message{

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
        System.out.printf("%d Received the message from %d  \n", host, message.getIdsrc());
        System.out.println("at "+System.currentTimeMillis());
        infra.exit();

    }

    @Override
    public void start () {
        System.out.println(infra.getId());

        if (infra.getId() == 0) {
            if (infra instanceof MpiInfrastructure) {
                MpiInfrastructure mp = (MpiInfrastructure) infra;
             //   mp.launchSimulation(fileName);
                System.out.println("j'envois je suis le process 0");

            }
            try {
                for (int i = 0; i < 3; i++) {
                    System.out.println("j'envois je suis le process 0");
                    infra.send(new ExampleMessage(infra.getId(),0,"Sent at "+System.currentTimeMillis()));
                    Thread.sleep(4000);
                }
            } catch (InterruptedException e) {
                System.err.println("No no");
            }
        }
    }


    @Test
    public void MpitesteProtocolBreakDown(){
        Ppi.main(new String[] {NodeBreakDownTest.class.getName(), MpiRunner.class.getName() ,"3" });

        assertTrue(true);
        System.out.println("Teste BreakDown from Json mpi ok");
    }

    @Test
    public void PeersimtesteProtocolBreakDown(){
        Ppi.main(new String[] { NodeBreakDownTest.class.getName(), PeerSimRunSimulation.class.getName() ,"3" , fileName});
        assertTrue(true);
        System.out.println("Teste BreakDown from Json Peersim ok");
    }

    @AfterClass
    public static void  after(){
        /*try {
            Files.deleteIfExists(Paths.get(fileName));
            System.out.println("End node down Test");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}*/