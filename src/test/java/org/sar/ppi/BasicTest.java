package org.sar.ppi;

import org.junit.Test;

import peersim.Simulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.assertTrue;

public class BasicTest {
	/*
    @Test
    public void runFirstBasicTest() throws IOException {

        String s = null;
        Process p = Runtime.getRuntime().exec("./mpirunjava.sh 6 Ppi org.sar.ppi.ExampleNodeProcess org.sar.ppi.MpiInfrastructure");
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));


        // read the output from the command
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
        // read any errors from the attempted command
        while ((s = stdError.readLine()) != null) {
            System.err.println(s);
        }
        assertTrue(true);
    }

    @Test
    public void runFirstBasicTestObject() throws IOException {

        String s = null;
        Process p = Runtime.getRuntime().exec("./mpirunjava.sh 6 Ppi org.sar.ppi.ExampleNodeProces org.sar.ppi.MpiInfrastructure");
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));


        // read the output from the command
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
        // read any errors from the attempted command
        while ((s = stdError.readLine()) != null) {
            System.err.println(s);
        }
        assertTrue(true);
    }
*/
    @Test
    public void firstTestPeerSim() {
    	
    	String[] tab=new String[1];
		tab[0]="notreconfig.conf";
		Simulator.main(tab);
		assertTrue(true);
    }

}
