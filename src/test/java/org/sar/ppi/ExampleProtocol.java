package org.sar.ppi;

/**
 * ExampleProtocol
 */
public class ExampleProtocol extends Protocol {

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
	public void startNode(int node) {
		if (node == 0) {
			infra.send(1, 0);
		}
	}
}