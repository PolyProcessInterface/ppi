package org.sar.ppi;

import java.net.URL;
import java.net.URLClassLoader;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Ppi Main class.
 */
@Command(name = "ppi", version = "ppi 0.2-dev")
public class Ppi implements Callable<Integer> {

	public static ClassLoader loader = ClassLoader.getSystemClassLoader();
	
	@Option(names = { "--np" }, paramLabel = "<number>", description = "Number of processus in the network")
	static int nbProcs = 4;
	
	@Option(names = { "-s", "--scenario" }, paramLabel = "<path>", description = "Path to the scenario file")
	static File scenario = null;

	@Parameters(paramLabel = "<process-class>", description = "Fully qualified name of the class to use as process")
	static String pClassName;

	@Parameters(paramLabel = "<runner-class>", description = "Fully qualified name of the class to use as runner")
	static String rClassName;

	@Option(names = { "-h", "--help" }, usageHelp = true, description = "display a help message")
	protected boolean help = false;

	/**
	 * The main to call to run the app. Usage:
	 * {@code java org.sar.Ppi <process-class-name> <runner-class-name> [<nb-proc> [<scenario>]]}
	 *
	 * This function sets the defaults values, then call the Runner's init method
	 * which should then call Ppi.main().
	 *
	 * @param args cli args.
	 * @throws PpiException on every internal error (TODO more Exceptions)
	 */
	public static void main(String[] args) {
		System.exit(new CommandLine(new Ppi()).execute(args));
	}

	@Override
	public Integer call() {
		Class<? extends NodeProcess> processClass;
		Runner runner;
		try {
			processClass = Class.forName(pClassName).asSubclass(NodeProcess.class);
		} catch (ClassNotFoundException e) {
			try {
				loader = new URLClassLoader(new URL[] { new File(System.getProperty("user.dir")).toURI().toURL() }, loader);
				processClass = loader.loadClass(pClassName).asSubclass(NodeProcess.class);
			} catch (ClassCastException | ClassNotFoundException | IOException t) {
				return exitWithError(1, "Could not find the process class %s", pClassName);
			}
		} catch (ClassCastException e) {
			return exitWithError(1, "The class %s does not extend NodeProcess", pClassName);
		}
		try {
			Class<? extends Runner> rClass = Class.forName(rClassName).asSubclass(Runner.class);
			runner = rClass.newInstance();
		} catch (ReflectiveOperationException e) {
			return exitWithError(2, "Failed to intanciate the Runner %s", rClassName);
		}
		try {
			main(processClass, runner, nbProcs, scenario);
		} catch (PpiException e) {
			return exitWithError(5, e.getMessage());
		}
		return 0;
	}

	/**
	 * Higher level main only required parameters for easier from java invocations.
	 * @param pClass the class to execute by Ppi.
	 * @param runner the runner to use for this execution.
	 * @throws PpiException if pClass instanciation fails.
	 */
	public static void main(Class<? extends NodeProcess> pClass, Runner runner) throws PpiException {
		main(pClass, runner, nbProcs, scenario);
	}

	/**
	 * Higher level main without optional parameters for easier from java invocations.
	 * @param pClass the class to execute by Ppi.
	 * @param runner the runner to use for this execution.
	 * @param nbProcs the number of processes to run.
	 * @throws PpiException if pClass instanciation fails.
	 */
	public static void main(Class<? extends NodeProcess> pClass, Runner runner, int nbProcs) throws PpiException {
		main(pClass, runner, nbProcs, scenario);
	}

	/**
	 * Higher level main for easier from java invocations.
	 *
	 * @param pClass the class to execute by Ppi.
	 * @param runner the runner to use for this execution.
	 * @param nbProcs the number of processes to run.
	 * @param scenario the name of the scenario file.
	 * @throws PpiException if pClass instanciation fails.
	 */
	public static void main(Class<? extends NodeProcess> pClass, Runner runner, int nbProcs, File scenario)
			throws PpiException {
		try {
			runner.run(pClass, nbProcs, scenario);
		} catch (ReflectiveOperationException e) {
			throw new PpiException("Failed to intantiate the process class " + pClass.getName(), e);
		}
	}

	private int exitWithError(int code, String message, Object... params) {
		System.err.printf(message + "\nuse --help for help\n", params);
		return code;
	}
}
