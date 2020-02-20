package org.sar.ppi;

/**
 * Protocol
 */
public abstract class Protocol {

	protected Infrastructure infra;

	public void setInfra(Infrastructure infra){
		this.infra = infra;
	}

	/**
	 * Handler to process a received message.
	 *
	 * @param src     node which sent the message.
	 * @param message the message received.
	 */
	public abstract void processMessage(Node src, Object message);

	/**
	 * Start execution sequence for one node.
	 *
	 * @param node the node to start.
	 */
	public abstract void startNode(Node node);
}