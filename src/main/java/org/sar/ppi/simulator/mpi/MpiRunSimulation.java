package org.sar.ppi.simulator.mpi;

import org.sar.ppi.NodeProcess;
import org.sar.ppi.Ppi;
import org.sar.ppi.PpiException;
import org.sar.ppi.Runner;
import org.sar.ppi.mpi.MpiInfrastructure;
import org.sar.ppi.mpi.MpiRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MpiRunSimulation implements Runner {
    public static void main(String[] args) {
        Ppi.run(args, new MpiRunner());
    }

    @Override
    public void init(String[] args) throws PpiException {
        String s = null;
        boolean err = false;
        String cmd = String.format(
                "mpirun --oversubscribe --np %s java -cp %s %s %s",
                args[2], System.getProperty("java.class.path"), this.getClass().getName(), args[0]);
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null)
                System.out.println(s);

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.err.println(s);
                err = true;
            }
            stdInput.close();
            stdError.close();
        } catch (IOException e) {
            throw new PpiException("Could not run MPI", e);
        }
        if (err) {
            throw new PpiException("An error occured with Mpi simulation runner");
        }
    }

    @Override
    public void run(Class<? extends NodeProcess> processClass, String[] options)
                throws ReflectiveOperationException {
        NodeProcess process = processClass.newInstance();
        MpiInfrastructure infra;
        if(options.length>1)
            infra= new MpiInfrastructure(process,options[1]);
        else
            infra = new MpiInfrastructure(process);
        process.setInfra(infra);
        infra.run(options);
        infra.exit();
    }
}