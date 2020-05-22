package org.sar.ppi.mpi;

import org.sar.ppi.NodeProcess;
import org.sar.ppi.Runner;

public class MpiSubRunner implements Runner {

	@Override
	public void run(Class<? extends NodeProcess> pClass, int nbProcs, String scenario)
			throws ReflectiveOperationException {
		NodeProcess process = pClass.newInstance();
		MpiInfrastructure infra = new MpiInfrastructure(process);
		process.setInfra(infra);
		infra.run(new String[0]);
		infra.exit();
	}
}