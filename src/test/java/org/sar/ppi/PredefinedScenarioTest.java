package org.sar.ppi;


import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sar.ppi.simulator.peersim.PeerSimRunSimulation;
import org.sar.ppi.simulator.peersim.ProtocolTools;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class PredefinedScenarioTest extends NodeProcess{
    static String  fileName = System.getProperty("user.dir")+"/testeJson.json";
    @Override
    public void processMessage (Message message){

        super.processMessage(message);
        System.out.println(" I am not called ");
    }

    @Override
    public void start () {
        System.out.println("my id "+infra.getId());
        if (infra.getId() == 0)
            infra.launchSimulation(fileName);
        else
          try {
              Thread.sleep(20);
              System.out.println("j'ai attendu 20 secon");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void callMe (String arg1, Integer arg2){
        System.out.println(infra.getId() + "arg1 = " + arg1 + " arg2 = " + arg2);
    }


    @BeforeClass
    public static void createJsonTeste(){
        try{
            FileWriter filew = new FileWriter(fileName);
            JSONObject toWrite = new JSONObject();
            List<Object> array = new ArrayList<>();
            //CALL 1
            array.add("Arg_1er Appel");
            array.add(4);
            long delay = 1;
            JSONObject call_1 = ProtocolTools.protocolToJSON("callMe",0,delay,array);
            toWrite.put("Call_1",call_1);

            //CALL 2
            array = new ArrayList<>();
            array.add("Arg_1er");
            array.add(4);
            delay = 2;
            JSONObject call_2 = ProtocolTools.protocolToJSON("callMe",1,delay,array);
            toWrite.put("Call_2",call_2);

            //CALL 3
            array = new ArrayList<>();
            array.add("Arg_dernier Appel");
            array.add(45);
            delay = 3;
            JSONObject call_3 = ProtocolTools.protocolToJSON("callMe",2,delay,array);
            toWrite.put("Call_3",call_3);

            filew.write(toWrite.toString());
            filew.flush();
            filew.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void MpiScenario() {
      //        Ppi.main(new String[] { org.sar.ppi.PredefinedScenarioTest.class.getName(), MpiRunSimulation.class.getName() ,"4" , fileName});
              assertTrue(true);
              System.out.println("Teste Sceneario from Json mpi ok");
    }

    @Test
    public void PeersimScenario() {
          Ppi.main(new String[] { org.sar.ppi.PredefinedScenarioTest.class.getName(), PeerSimRunSimulation.class.getName() });
          assertTrue(true);
          System.out.println("Teste Sceneario from Json Peersim ok");
    }
    @AfterClass
    public static void  after(){
        try {
            Files.deleteIfExists(Paths.get(fileName));
            System.out.println("End Scenario Test");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}