package org.sar.ppi;

import java.util.Arrays;

/**
 * Ppi
 */
public class Ppi {

	/**
	 * The main to call to run the app.
	 * Usage: java org.sar.Ppi <process-class-name> <runner-class-name> [<nb-proc>]
	 *
	 * This function sets the defaults values, then call the Runner's init method
	 * which should then call Ppi.main().
	 *
	 * @param args cli args.
	 * @throws PpiException
	 */
	public static void main(String[] args) throws PpiException {
		try {
			Class<? extends Runner> rClass = Class.forName(args[1]).asSubclass(Runner.class);
			if(args.length > 3)
				args = Arrays.copyOf(args, 4);
			else
				args = Arrays.copyOf(args, 3);

			if (args[2] == null) {
				args[2] = "5";
			}
			rClass.newInstance().init(args);
		} catch (ReflectiveOperationException e) {
			throw new PpiException("Failed to init the process", e);
		}
	}

	public static void run(String[] args, Runner runner) throws PpiException {
		try {
			Class<? extends NodeProcess> pClass = Class.forName(args[0]).asSubclass(NodeProcess.class);
			runner.run(pClass, args);
		} catch (ReflectiveOperationException e) {
			throw new PpiException("Failed to run the process", e);
		}
	}

}