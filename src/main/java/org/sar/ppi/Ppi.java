package org.sar.ppi;

/**
 * Ppi
 */
public class Ppi {

	public static void main(String[] args) throws PpiException {
		try {
			Class<? extends NodeProcess> pClass = Class.forName(args[0]).asSubclass(NodeProcess.class);
			Class<? extends Infrastructure> iClass = Class.forName(args[1]).asSubclass(Infrastructure.class);
			NodeProcess process = pClass.newInstance();
			Infrastructure infra = iClass.getConstructor(NodeProcess.class).newInstance(process);
			process.setInfra(infra);
			infra.run(args);
			infra.exit();
		} catch (ReflectiveOperationException e) {
			throw new PpiException("Failed to run the process", e);
		}
	}

}