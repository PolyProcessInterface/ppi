package org.sar.ppi;

public abstract class Infrastructure {

	protected Protocol protocol;
	protected int currentNode;

	public Infrastructure(Protocol protocol) {
		this.protocol = protocol;
	}

	/**
	 * @return the currentNode
	 */
	public int getId() {
		return currentNode;
	}

	/**
	 * Start the infrastructure.
	 *
	 * @param args arguments to pass to pass to the infrastructure.
	 */
	public abstract void run(String[] args);

	/**
	 * Send a message to node.
	 *
	 * @param dest    the destination node.
	 * @param message the message to send.
	 */
	public abstract void send(int dest, Object message);

	/**
	 * Stop the execution of the infrastructure for th current node.
	 */
	public abstract void exit();

	/**
	 * Return the number of nodes in the infrastructure.
	 *
	 * @return number of nodes.
	 */
	public abstract int size();
}