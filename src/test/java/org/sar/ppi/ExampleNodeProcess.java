package org.sar.ppi;

/**
 * ExampleNodeProces
 */
public class ExampleNodeProcess extends NodeProcess {

	@Override
	public void processMessage(int src, Object message) {
		int host = infra.getId();
		System.out.println("" + host + " Received hello from "  + src);
		if (host != 0) {
			infra.send((host + 1) % infra.size(), 0);
		}
		infra.exit();
	}

	@Override
	public void start() {
		if (infra.getId() == 0) {
			infra.send(1, 0);
		}
	}
}