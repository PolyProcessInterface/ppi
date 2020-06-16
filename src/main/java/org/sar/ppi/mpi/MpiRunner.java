package org.sar.ppi.mpi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sar.ppi.NodeProcess;
import org.sar.ppi.Ppi;
import org.sar.ppi.PpiException;
import org.sar.ppi.Runner;
import org.sar.ppi.events.Scenario;

/**
 * MpiRunner class.
 */
public class MpiRunner implements Runner {
	private static final Logger logger = LogManager.getLogger();

	/** {@inheritDoc} */
	@Override
	public void run(Class<? extends NodeProcess> pClass, String[] args, int nbProcs, Scenario scenario) throws PpiException {
		String scenarioJson;
		String s = null;
		boolean err = false;
		try {
			scenarioJson = Ppi.getMapper().writeValueAsString(scenario);
		} catch (JsonProcessingException e) {
			throw new PpiException("Could not serialize this scenario", e);
		}
		ProcessBuilder pBuilder = new ProcessBuilder(
			"mpirun",
			"--oversubscribe",
			"--np",
			String.valueOf(nbProcs),
			"java",
			"-cp",
			System.getProperty("java.class.path"),
			Ppi.class.getName(),
			pClass.getName(),
			MpiSubRunner.class.getName(),
			"--np=" + nbProcs,
			"-c=" + scenarioJson,
			String.join(" ", args)
		);
		logger.debug("mpi cmdline: {}", pBuilder.command());
		try {
			Process p = pBuilder.start();
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
