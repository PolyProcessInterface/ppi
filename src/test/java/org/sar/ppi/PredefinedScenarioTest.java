package org.sar.ppi;


import org.json.simple.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sar.ppi.mpi.MpiRunner;
import org.sar.ppi.peersim.PeerSimRunner;
import org.sar.ppi.simulator.ProtocolTools;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class PredefinedScenarioTest{

        public class ScenarioClass  extends NodeProcess{

            @Override
            public void processMessage (Message message){
                System.out.println(" I am not called ");
            }

            @Override
            public void start () {
            //if (infra.getId() == 0)
            //infra.launchSimulation();
            }


            public void callMe (String arg1, Integer arg2){
                System.out.println(infra.getId() + "arg1 = " + arg1 + " arg2 = " + arg2);
            }
        }


    String fileName = "testeJson.json";

    @BeforeClass
    public void createJsonTeste(){
        try{
            FileWriter filew = new FileWriter(fileName)
            JSONObject toWrite = new JSONObject();
            List<Object> array = new ArrayList<>();
            //CALL 1
            array.add("Arg_1er Appel");
            array.add(4);
            long delay = 50;
            JSONObject call_1 = ProtocolTools.protocolToJSON("callMe",1,delay,array);
            toWrite.put("Call_1",call_1);

            //CALL 2
            array.add("Arg_1er Appel");
            array.add(4);
            delay = 100;
            JSONObject call_2 = ProtocolTools.protocolToJSON("callMe",2,delay,array);
            toWrite.put("call_2",call_2);

            //CALL 3
            array = new ArrayList<>();
            array.add("Arg_dernier Appel");
            array.add(45);
            delay = 150;
            JSONObject call_3 = ProtocolTools.protocolToJSON("callMe",3,delay,array);
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
        //        Ppi.main(new String[] { org.sar.ppi.NodeProcessTest.class.getName(), MpiRunner.class.getName() });
        //      assertTrue(true);
    }

    @Test
    public void PeersimScenario() {
//            Ppi.main(new String[] { org.sar.ppi.NodeProcessTest.class.getName(), PeerSimRunner.class.getName() });
        //          assertTrue(true);
    }


}
