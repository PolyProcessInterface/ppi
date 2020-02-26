package org.sar.ppi;

public abstract class Infrastructure {

	protected NodeProcess process;
	protected int currentNode;

	public Infrastructure(NodeProcess process) {
		this.process = process;
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
	public abstract void send(Message message);

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