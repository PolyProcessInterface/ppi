package org.sar.ppi;

/**
 * Runner Interface. It is run by Ppi to start the Infractructure.
 */
public interface Runner {
	/**
	 * Run the runner.
	 * @param pClass   the class to execute by Ppi.
	 * @param args     the args to pass to the processes.
	 * @param nbProcs  the number of processes to run.
	 * @param config   the config to execute.
	 * @throws java.lang.ReflectiveOperationException if pClass instanciation fails.
	 */
	public void run(Class<? extends NodeProcess> pClass, String[] args, int nbProcs, Config config)
		throws ReflectiveOperationException;
}
