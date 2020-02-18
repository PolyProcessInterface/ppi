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

    public abstract void run(String[] args);
    public abstract void send(Node dest, Object message);
    public abstract void broadcast(Object messsage);
}