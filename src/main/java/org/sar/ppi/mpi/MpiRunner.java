package org.sar.ppi.mpi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.sar.ppi.NodeProcess;
import org.sar.ppi.Ppi;
import org.sar.ppi.PpiException;
import org.sar.ppi.Runner;

/**
 * MpiRunner class.
 */
public class MpiRunner implements Runner {

	/** {@inheritDoc} */
	@Override
	public void run(Class<? extends NodeProcess> pClass, String[] args, int nbProcs, File scenario) throws PpiException {
		String s = null;
		boolean err = false;
		String cmd = String.format(
			"mpirun --oversubscribe --np %s java -cp %s %s %s %s --np=%d %s %s",
			nbProcs,
			System.getProperty("java.class.path"),
			Ppi.class.getName(),
			pClass.getName(),
			MpiSubRunner.class.getName(),
			nbProcs,
			scenario != null ? "-s" + scenario.getAbsolutePath() : "",
			String.join(" ", args)
		);
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			Thread killMpi = new Thread(() -> {
				System.out.println("Interrupt received, killing MPI");
				p.destroyForcibly();
				try { p.waitFor(1, TimeUnit.SECONDS); } catch (InterruptedException e) {}
				p.destroy();
			});
			Runtime.getRuntime().addShutdownHook(killMpi);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			// read the output from the command
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}
			// read any errors from the attempted command
			while ((s = stdError.readLine()) != null) {
				System.err.println(s);
				err = true;
			}
			stdInput.close();
			stdError.close();
			p.waitFor();
			Runtime.getRuntime().removeShutdownHook(killMpi);
		} catch (IOException e) {
			throw new PpiException("Could not run MPI", e);
		} catch (InterruptedException e) {
			throw new PpiException("Interrupted while waiting for MPI process", e);
		}
		if (err) {
			throw new PpiException("An error occured with Mpi");
		}
	}
}
