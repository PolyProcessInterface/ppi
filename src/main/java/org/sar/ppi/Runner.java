package org.sar.ppi;

/**
 * Runner Interface. It is run by Ppi to start the Infractructure.
 */
public interface Runner {

	/**
	 * Run the runner.
	 *
	 * @param pClass the class to execute by Ppi.
	 * @param nbProcs the number of processes to run.
	 * @param scenario the name of the scenario file.
	 * @throws java.lang.ReflectiveOperationException if pClass instanciation fails.
	 */
	public void run(Class<? extends NodeProcess> pClass, int nbProcs, String scenario) throws ReflectiveOperationException;
}
