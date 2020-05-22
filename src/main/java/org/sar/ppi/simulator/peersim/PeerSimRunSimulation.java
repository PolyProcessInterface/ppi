package org.sar.ppi.simulator.peersim;

import org.sar.ppi.NodeProcess;
import org.sar.ppi.PpiException;
import org.sar.ppi.Runner;
import peersim.Simulator;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PeerSimRunSimulation implements Runner {
    @Override
    public void run(Class<? extends NodeProcess> pClass, int nbProcs, String scenario) {
        if (scenario == null)
            throw new PpiException("<scenario> parameter is needed for this Runner");

        String tmpdir = System.getProperty("java.io.tmpdir");
        String tmpfile = Paths.get(tmpdir, "ppi-peersim.config").toString();
        try (OutputStream os = new FileOutputStream(tmpfile);
             PrintStream ps = new PrintStream(os))
        {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Path base = Paths.get(loader.getResource("peersimSimulate.base.conf").toURI());
            Files.copy(base, os);
            ps.println();
            ps.println("protocol.infra.nodeprocess " + pClass.getName());
            ps.printf("network.size %d\n", nbProcs);
            ps.println("path " + scenario);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] tab = new String[1];
        tab[0] = tmpfile;
        Simulator.main(tab);
    }}
