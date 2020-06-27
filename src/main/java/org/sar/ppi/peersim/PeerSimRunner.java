package org.sar.ppi.peersim;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sar.ppi.Config;
import org.sar.ppi.NodeProcess;
import org.sar.ppi.Runner;
import org.sar.ppi.tools.PpiUtils;
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
		String tmpfile = Paths.get(tmpdir, "ppi-peersim.config").toString();
		LOGGER.debug("peersim config file: '{}'", config.getInfraProp("configFile", ""));
		try (
			OutputStream os = new FileOutputStream(tmpfile);
			PrintStream ps = new PrintStream(os)
		) {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			InputStream base;
			base = loader.getResourceAsStream("peersimSimulate.base.conf");
			assert base != null;
			PpiUtils.transferTo(base, os);
			ps.println();
			ps.println("protocol.infra.nodeprocess " + pClass.getName());
			ps.printf("network.size %d\n", nbProcs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		PeerSimInfrastructure.setArgs(args);
		String[] tab = new String[1];
		tab[0] = tmpfile;
		Simulator.main(tab);
	}
}
