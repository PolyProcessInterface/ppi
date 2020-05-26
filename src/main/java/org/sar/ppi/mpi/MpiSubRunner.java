package org.sar.ppi.mpi;

import org.sar.ppi.NodeProcess;
import org.sar.ppi.Runner;

/**
 * MpiSubRunner class.
 */
public class MpiSubRunner implements Runner {

	/** {@inheritDoc} */
	@Override
	public void run(Class<? extends NodeProcess> pClass, int nbProcs, String scenario)
			throws ReflectiveOperationException {
		NodeProcess process = pClass.newInstance();
		MpiInfrastructure infra;
		if(scenario.equals("no"))
			infra = new MpiInfrastructure(process);

		else
			infra = new MpiInfrastructure(process, scenario);
		process.setInfra(infra);
		infra.run(new String[0]);
		infra.exit();
	}
}
