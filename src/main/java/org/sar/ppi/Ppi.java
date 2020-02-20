package org.sar.ppi;

/**
 * Ppi
 */
public class Ppi {

	public static void main(String[] args) throws PpiException {
		try {
			Class<? extends Protocol> pClass = Class.forName(args[0]).asSubclass(Protocol.class);
			Class<? extends Infrastructure> iClass = Class.forName(args[1]).asSubclass(Infrastructure.class);
			Protocol protocol = pClass.newInstance();
			Infrastructure infra = iClass.getConstructor(Protocol.class).newInstance(protocol);
			protocol.setInfra(infra);
			infra.run(args);
			infra.exit();
		} catch (ReflectiveOperationException e) {
			throw new PpiException("Failed to run the protocol", e);
		}
	}

}