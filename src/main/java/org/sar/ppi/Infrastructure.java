package org.sar.ppi;


import java.util.Timer;

public abstract class Infrastructure {

	protected NodeProcess process;
	protected int currentNode;
	protected Timer timer = new Timer();
	public Infrastructure(NodeProcess process) {
		this.process = process;
	}

	/**
	 * Send a message to node.
	 *
	 * @param message the message to send.
	 */
	public abstract void send(Message message);

	public abstract void launchSimulation(String path);


	/**
	 * @return the currentNode
	 */
	public int getId() {
		return currentNode;
	}


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