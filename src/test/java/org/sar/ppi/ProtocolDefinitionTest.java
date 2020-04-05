package org.sar.ppi;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
            System.out.println("Teste Description ok");
            Files.deleteIfExists(Paths.get(fileName));
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
