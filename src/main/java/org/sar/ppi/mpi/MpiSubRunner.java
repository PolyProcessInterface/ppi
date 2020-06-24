package org.sar.ppi.mpi;

import org.sar.ppi.Config;
import org.sar.ppi.NodeProcess;
import org.sar.ppi.Runner;

/**
 * MpiSubRunner class.
 */
public class MpiSubRunner implements Runner {

	/** {@inheritDoc} */
	@Override
	public void run(Class<? extends NodeProcess> pClass, String[] args, int nbProcs, Config config)
		throws ReflectiveOperationException {
		NodeProcess process = pClass.newInstance();
		MpiInfrastructure infra;
		infra = new MpiInfrastructure(process, config);
		process.setInfra(infra);
		infra.run(args);
	}
}
