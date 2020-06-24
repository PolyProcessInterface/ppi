package org.sar.ppi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Ppi Main class.
 */
@Command(name = "ppi", versionProvider = ManifestVersionProvider.class)
public class Ppi implements Callable<Integer> {
	private static final Logger LOGGER = LogManager.getLogger();
	protected static ObjectMapper mapper = new ObjectMapper();
	protected static Config config;

	public static ClassLoader loader = ClassLoader.getSystemClassLoader();

	@Option(
		names = { "--np" },
		paramLabel = "<number>",
		description = "Number of processus in the network",
		showDefaultValue = Visibility.ALWAYS
	)
	static int nbProcs = 4;

	@ArgGroup(exclusive = true, multiplicity = "0..1")
	protected static ConfigOption configOption = new ConfigOption();

	static class ConfigOption {
		@Option(
			names = { "-c", "--config" },
			paramLabel = "<path>",
			description = "Path to the config file"
		)
		protected File configPath = null;

		@Option(
			names = { "-j", "--json" },
			paramLabel = "<content>",
			description = "Content of the config"
		)
		protected String configJson = null;

		public Config get() {
			Config config;
			if (configJson != null) {
				config = parseConfig(configJson);
			} else if (configPath != null) {
				config = parseConfig(configPath);
			} else {
				config = new Config();
			}
			return config;
		}
	}

	@Parameters(
		paramLabel = "<process-class>",
		description = "Fully qualified name of the class to use as process",
		index = "0"
	)
	static String pClassName;

	@Parameters(
		paramLabel = "<runner-class>",
		description = "Fully qualified name of the class to use as runner",
		index = "1"
	)
	static String rClassName;

	@Parameters(
		paramLabel = "<args>",
		arity = "0..*",
		description = "Args to pass to the processes",
		index = "2..*"
	)
	static String[] args = new String[0];

	@Option(names = { "-h", "--help" }, usageHelp = true, description = "Display a help message")
	protected boolean help = false;

	@Option(names = { "-V", "--version" }, versionHelp = true, description = "Print version info")
	protected boolean version = false;

	// Configure the ObjectMapper once
	static {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/**
	 * The main to call to run the app.
	 *
	 * This function sets the defaults values, then call the Runner's init method
	 * which should then call Ppi.main().
	 *
	 * @param args cli args.
	 */
	public static void main(String[] args) {
		System.exit(new CommandLine(new Ppi()).setTrimQuotes(true).execute(args));
	}

	/**
	 * Process to execute after the CLI args parsing.
	 */
	@Override
	public Integer call() {
		Class<? extends NodeProcess> processClass;
		Runner runner;
		try {
			processClass = Class.forName(pClassName).asSubclass(NodeProcess.class);
		} catch (ClassNotFoundException e) {
			try {
				File pwd = new File(System.getProperty("user.dir"));
				loader = new URLClassLoader(new URL[] { pwd.toURI().toURL() }, loader);
				processClass = loader.loadClass(pClassName).asSubclass(NodeProcess.class);
			} catch (ClassCastException | ClassNotFoundException | IOException t) {
				return exitWithError(1, "Could not find the process class %s", pClassName);
			}
		} catch (ClassCastException e) {
			return exitWithError(2, "The class %s does not extend NodeProcess", pClassName);
		}
		try {
			Class<? extends Runner> rClass = Class.forName(rClassName).asSubclass(Runner.class);
			runner = rClass.newInstance();
		} catch (ReflectiveOperationException e) {
			return exitWithError(3, "Failed to intanciate the Runner %s", rClassName);
		}
		try {
			main(processClass, runner, args, nbProcs, configOption.get());
		} catch (PpiException e) {
			return exitWithError(5, e.getMessage());
		}
		return 0;
	}

	/**
	 * Higher level main only required parameters for easier from java invocations.
	 * @param pClass        the class to execute by Ppi.
	 * @param runner        the runner to use for this execution.
	 * @throws PpiException if pClass instanciation fails.
	 */
	public static void main(Class<? extends NodeProcess> pClass, Runner runner)
		throws PpiException {
		main(pClass, runner, new String[0], nbProcs, configOption.get());
	}

	/**
	 * Higher level main without optional parameters for easier from java invocations.
	 * @param pClass        the class to execute by Ppi.
	 * @param runner        the runner to use for this execution.
	 * @param args          the args to pass to the processes.
	 * @throws PpiException if pClass instanciation fails.
	 */
	public static void main(Class<? extends NodeProcess> pClass, Runner runner, String[] args)
		throws PpiException {
		main(pClass, runner, args, nbProcs, configOption.get());
	}

	/**
	 * Higher level main without optional parameters for easier from java invocations.
	 * @param pClass        the class to execute by Ppi.
	 * @param runner        the runner to use for this execution.
	 * @param args          the args to pass to the processes.
	 * @param nbProcs       the number of processes to run.
	 * @throws PpiException if pClass instanciation fails.
	 */
	public static void main(
		Class<? extends NodeProcess> pClass,
		Runner runner,
		String[] args,
		int nbProcs
	)
		throws PpiException {
		main(pClass, runner, args, nbProcs, configOption.get());
	}

	/**
	 * Higher level main for easier from java invocations.
	 * @param pClass        the class to execute by Ppi.
	 * @param runner        the runner to use for this execution.
	 * @param args          the args to pass to the processes.
	 * @param nbProcs       the number of processes to run.
	 * @param configFile    the name of the config file.
	 * @throws PpiException if pClass instanciation fails.
	 */
	public static void main(
		Class<? extends NodeProcess> pClass,
		Runner runner,
		String[] args,
		int nbProcs,
		File configFile
	)
		throws PpiException {
		main(pClass, runner, args, nbProcs, parseConfig(configFile));
	}

	/**
	 * Higher level main for easier from java invocations.
	 * @param pClass        the class to execute by Ppi.
	 * @param runner        the runner to use for this execution.
	 * @param args          the args to pass to the processes.
	 * @param nbProcs       the number of processes to run.
	 * @param config        the content of the config.
	 * @throws PpiException if pClass instanciation fails.
	 */
	public static void main(
		Class<? extends NodeProcess> pClass,
		Runner runner,
		String[] args,
		int nbProcs,
		Config config
	)
		throws PpiException {
		try {
			Ppi.config = config;
			runner.run(pClass, args, nbProcs, config);
		} catch (ReflectiveOperationException e) {
			throw new PpiException("Failed to intantiate the process class " + pClass.getName(), e);
		}
	}

	public static ObjectMapper getMapper() {
		return mapper;
	}

	public static Config getConfig() {
		return config;
	}

	private static Config parseConfig(String json) {
		if (json == null) {
			return new Config();
		}
		// temporary fix while https://github.com/remkop/picocli/issues/1113 is open.
		if (json.length() > 1 && json.startsWith("'") && json.endsWith("'")) {
			json = json.substring(1, json.length() - 1);
		}
		try {
			return mapper.readValue(json, Config.class);
		} catch (IOException e) {
			LOGGER.debug("escaped json: {}", json);
			LOGGER.error(e.getMessage());
			throw new PpiException("Invalid config json", e);
		}
	}

	private static Config parseConfig(File file) {
		if (file == null) {
			return new Config();
		}
		try {
			return mapper.readValue(file, Config.class);
		} catch (IOException e) {
			throw new PpiException("Invalid config file", e);
		}
	}

	private int exitWithError(int code, String message, Object... params) {
		System.err.printf(message + "\nuse --help for help\n", params);
		return code;
	}
}
