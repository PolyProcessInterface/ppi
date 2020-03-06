package org.sar.ppi.peersim;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.sar.ppi.NodeProcess;
import org.sar.ppi.Runner;

import peersim.Simulator;

/**
 * PeerSimRunner
 */
public class PeerSimRunner implements Runner {

	@Override
	public void run(Class<? extends NodeProcess> processClass, String[] args)
			throws ReflectiveOperationException {
		String tmpdir = System.getProperty("java.io.tmpdir");
		String tmpfile = Paths.get(tmpdir, "ppi-peersim.config").toString();
		try (OutputStream os = new FileOutputStream(tmpfile);
			 PrintStream ps = new PrintStream(os))
		{
			Files.copy(Paths.get("peersim.base.conf"), os);
			ps.println();
			ps.println("protocol.infra.nodeprocess " + processClass.getName());
			ps.println("network.size " + args[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] tab = new String[1];
		tab[0] = tmpfile;
		Simulator.main(tab);
	}
}