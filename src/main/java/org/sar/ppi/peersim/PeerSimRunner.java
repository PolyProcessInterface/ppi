package org.sar.ppi.peersim;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;

import org.sar.ppi.NodeProcess;
import org.sar.ppi.Runner;
import org.sar.ppi.Utils;

import peersim.Simulator;

/**
 * PeerSimRunner class.
 */
public class PeerSimRunner implements Runner {

	/** {@inheritDoc} */
	@Override
	public void run(Class<? extends NodeProcess> pClass, int nbProcs, String scenario)
			throws ReflectiveOperationException {
		String tmpdir = System.getProperty("java.io.tmpdir");
		String tmpfile = Paths.get(tmpdir, "ppi-peersim.config").toString();
		try (OutputStream os = new FileOutputStream(tmpfile);
			 PrintStream ps = new PrintStream(os))
		{
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			InputStream base = loader.getResourceAsStream("peersim.base.conf");
			Utils.transferTo(base, os);
			ps.println();
			ps.println("protocol.infra.nodeprocess " + pClass.getName());
			ps.printf("network.size %d\n", nbProcs);
			if(scenario!=null)
				ps.println("path " + scenario);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] tab = new String[1];
		tab[0] = tmpfile;
		Simulator.main(tab);
	}
}
