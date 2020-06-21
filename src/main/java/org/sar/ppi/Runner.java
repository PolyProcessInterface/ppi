package org.sar.ppi;

import org.sar.ppi.events.Scenario;

/**
 * Runner Interface. It is run by Ppi to start the Infractructure.
 */
public interface Runner {
	/**
	 * Run the runner.
	 * @param pClass   the class to execute by Ppi.
	 * @param args     the args to pass to the processes.
	 * @param nbProcs  the number of processes to run.
	 * @param scenario the scenario to execute.
	 * @throws java.lang.ReflectiveOperationException if pClass instanciation fails.
	 */
	void run(Class<? extends NodeProcess> pClass, String[] args, int nbProcs, Scenario scenario)
		throws ReflectiveOperationException;
}
