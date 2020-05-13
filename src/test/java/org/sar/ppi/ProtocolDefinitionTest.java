package org.sar.ppi;


import org.json.simple.JSONObject;
import org.junit.Test;
import org.sar.ppi.simulator.peersim.ProtocolTools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.List;

public class ProtocolDefinitionTest {
    @Test
    public void ScenarioDescriptionTeste(){
        String fileName = "teste";
        try(FileOutputStream file1 = new FileOutputStream(fileName);
            FileInputStream file2 = new FileInputStream(fileName);
            ObjectOutputStream in = new ObjectOutputStream(file1);
            ObjectInputStream out = new ObjectInputStream(file2)){
            List<Object> array = new ArrayList<>();
            array.add("arg_1");
            array.add("arg_2");
            ProtocolTools.writeTimeFuncCall("FuncName",0,4,array,in);
            Object[] func_object= ProtocolTools.readTimeFuncCall(out);
            assertEquals("FuncName", func_object[0]);
            assertEquals(0, func_object[1]);
            long l = 4;
            assertEquals("arg_1",func_object[3]);
            assertEquals("arg_2",func_object[4]);
            System.out.println("Teste Description Stream ok");
            Files.deleteIfExists(Paths.get(fileName));
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void JsonDescriptionTeste(){
        String fileName = "testeJson.json";
        try(FileWriter filew = new FileWriter(fileName)){
            //in
            JSONObject toWrite = new JSONObject();
            List<Object> array = new ArrayList<>();
            array.add("arg_String");
            array.add(14);
            array.add(13);
            long delay = 15;
            JSONObject call_1 = ProtocolTools.protocolToJSON("Name_01",3,delay,array);
            toWrite.put("Call_1",call_1);
            toWrite.put("Call_2",ProtocolTools.protocolToJSON("func_no_args",5,delay,new ArrayList<>()));
            filew.write(toWrite.toString());
            filew.flush();
            filew.close();

            //Out
            List<Object[]> array_obj = ProtocolTools.readProtocolJSON(fileName);
            assertEquals(2,array_obj.size());
            Object[] res_call1 = array_obj.get(0);
            Object[] res_call2 = array_obj.get(1);

            assertTrue(res_call1[0] instanceof String);
            assertTrue(res_call2[0] instanceof String);
            assertTrue(res_call1[1] instanceof Integer);
            assertTrue(res_call2[1] instanceof Integer);

            assertTrue(res_call1[2] instanceof Long);
            assertTrue(res_call2[2] instanceof Long);

            System.out.println("Teste Description Json ok");
            Files.deleteIfExists(Paths.get(fileName));
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }


}
