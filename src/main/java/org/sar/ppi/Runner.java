package org.sar.ppi;

/**
 * Runner Interface. It is run by Ppi to start the Infractructure.
 */
public interface Runner {
	/**
	 * Get the name of this Runner's Infrastructure.
	 * This name will be used primarily as the key for this infrastructure's specific
	 * properties in the config file.
	 *
	 * @return the name of this Runner's Infrastructure.
	 */
	String getName();

	/**
	 * Run the runner.
	 * @param pClass   the class to execute by Ppi.
	 * @param args     the args to pass to the processes.
	 * @param nbProcs  the number of processes to run.
	 * @param config   the config to execute.
	 * @throws java.lang.ReflectiveOperationException if pClass instanciation fails.
	 */
	void run(Class<? extends NodeProcess> pClass, String[] args, int nbProcs, Config config)
		throws ReflectiveOperationException;
}
