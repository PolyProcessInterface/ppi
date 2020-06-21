package org.sar.ppi.peersim;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import org.sar.ppi.NodeProcess;
import org.sar.ppi.Runner;
import org.sar.ppi.events.Scenario;
import org.sar.ppi.tools.PpiUtils;
import peersim.Simulator;

/**
 * PeerSimRunner class.
 */
public class PeerSimRunner implements Runner {

	/** {@inheritDoc} */
	@Override
	public void run(
		Class<? extends NodeProcess> pClass,
		String[] args,
		int nbProcs,
		Scenario scenario
	)
		throws ReflectiveOperationException {
		String tmpdir = System.getProperty("java.io.tmpdir");
		String tmpfile = Paths.get(tmpdir, "ppi-peersim.config").toString();
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
