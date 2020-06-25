package org.sar.ppi.mpi;

import org.sar.ppi.NodeProcess;
import org.sar.ppi.Runner;
import org.sar.ppi.events.Scenario;

/**
 * MpiSubRunner class.
 */
public class MpiSubRunner implements Runner {

	/** {@inheritDoc} */
	@Override
	public void run(
		Class<? extends NodeProcess> pClass,
		String[] args,
		int nbProcs,
		Scenario scenario
	)
		throws ReflectiveOperationException {
		NodeProcess process = pClass.newInstance();
		MpiInfrastructure infra;
		infra = new MpiInfrastructure(process, scenario);
		process.setInfra(infra);
		infra.run(args);
	}
}
