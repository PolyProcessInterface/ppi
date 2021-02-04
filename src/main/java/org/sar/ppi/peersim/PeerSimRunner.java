package org.sar.ppi.peersim;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sar.ppi.Config;
import org.sar.ppi.NodeProcess;
import org.sar.ppi.Runner;
import peersim.Simulator;

/**
 * PeerSimRunner class.
 */
public class PeerSimRunner implements Runner {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String NAME = "peersim";

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return NAME;
	}

	/** {@inheritDoc} */
	@Override
	public void run(Class<? extends NodeProcess> pClass, String[] args, int nbProcs, Config config)
		throws ReflectiveOperationException {
		String tmpdir = System.getProperty("java.io.tmpdir");
		String tmpfile = Paths.get(tmpdir, "ppi-peersim.properties").toString();
		String userconfig = config.getInfraProp("properties", "");
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		LOGGER.debug("peersim config file: '{}'", userconfig);
		try (OutputStream os = new FileOutputStream(tmpfile);) {
			Properties properties = new Properties();
			properties.load(loader.getResourceAsStream("peersim.default.properties"));
			if (!userconfig.isEmpty()) {
				Properties overrides = new Properties();
				try (InputStream is = new FileInputStream(userconfig)) {
					overrides.load(is);
					properties.putAll(overrides);
				} catch (IOException e) {
					LOGGER.warn("Invalid peersim properties file, ignoring it");
				}
			}
			properties.setProperty("protocol.infra.nodeprocess", pClass.getName());
			properties.setProperty("network.size", String.valueOf(nbProcs));
			properties.store(os, "tmp properties");
		} catch (Exception e) {
			e.printStackTrace();
		}
		PeerSimInfrastructure.setArgs(args);
		String[] tab = new String[1];
		tab[0] = tmpfile;
		Simulator.main(tab);
	}
}
