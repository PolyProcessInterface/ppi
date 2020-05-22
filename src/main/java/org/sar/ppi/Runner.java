package org.sar.ppi;

/**
 * Runner
 */
public interface Runner {

	/**
	 * The init sequence for this Runner.
	 * If nothing needs to be prepared before the run method, then the default
	 * implementation can be left as it is.
	 *
	 * @param args the cli args.
	 */
	default public void init(String[] args) throws PpiException {
		Ppi.run(args, this);
	}

	/**
	 * 
	 * @param processClass the user extension of NodeProcess
	 * @param args cli arguments
	 * @throws ReflectiveOperationException if processClass instanciation fails
	 */
	public void run(Class<? extends NodeProcess> processClass, String[] args)
			throws ReflectiveOperationException;
}