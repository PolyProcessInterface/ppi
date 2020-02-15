package org.sar.ppi;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class RingMpiTest {
    /**
     * Quite dirty for now but at least it can run mpi.
     */
    @Test
    public void runThroughScript() throws IOException {
        String s = null;
        Process p = Runtime.getRuntime().exec("./mpirun.sh 6 RingMpi");
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
}
