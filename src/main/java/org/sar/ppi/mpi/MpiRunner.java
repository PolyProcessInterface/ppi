package org.sar.ppi.mpi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.sar.ppi.NodeProcess;
import org.sar.ppi.Ppi;
import org.sar.ppi.PpiException;
import org.sar.ppi.Runner;

/**
 * MpiRunner
 */
public class MpiRunner implements Runner {

	public static void main(String[] args) {
		Ppi.run(args, new MpiRunner());
	}

	@Override
	public void init(String[] args) throws PpiException {
		String s = null;
		try {
			// TODO the 6 should be replaced by a parameter read from the args.
			StringBuilder sb = new StringBuilder("./mpirunjava.sh 6 mpi.MpiRunner ").append(args[0]);
			Process p = Runtime.getRuntime().exec(sb.toString());
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run(Class<? extends NodeProcess> processClass, String[] args)
			throws ReflectiveOperationException {
		NodeProcess process = processClass.newInstance();
		MpiInfrastructure infra = new MpiInfrastructure(process);
		process.setInfra(infra);
		infra.run(args);
		infra.exit();
	}

}