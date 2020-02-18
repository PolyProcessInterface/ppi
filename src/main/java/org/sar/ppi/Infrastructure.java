package org.sar.ppi;

public abstract class Infrastructure {

    protected Protocol protocol;
    protected Node currentNode;

    public Infrastructure(Protocol protocol) {
        this.protocol = protocol;
    }

    /**
     * @return the currentNode
     */
    public Node getCurrentNode() {
        return currentNode;
    }

    /**
     * Start the infrastructure.
     * @param args arguments to pass to pass to the infrastructure.
     */
    public abstract void run(String[] args);

    /**
     * Send a message to node.
     * @param dest the destination node.
     * @param message the message to send.
     */
    public abstract void send(Node dest, Object message);

    /**
     * Get a node in the infrastructure from its id.
     * @param id the id of the node.
     * @return the node.
     */
    public abstract Node getNode(int id);

    /**
     * Broadcast a message to all the other nodes.
     * @param messsage the message to broadcast.
     */
    public abstract void broadcast(Object messsage);

    /**
     * Stop the execution of the infrastructure for th current node.
     */
    public abstract void exit();

    /**
     * Return the number of nodes in the infrastructure.
     * @return number of nodes.
     */
	public abstract int size();
}