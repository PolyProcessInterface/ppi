package org.sar.ppi;

/**
 * Process
 */
public abstract class NodeProcess {

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
	public abstract void processMessage(int src, Object message);

	/**
	 * Start execution sequence for the current node.
	 */
	public abstract void start();
}