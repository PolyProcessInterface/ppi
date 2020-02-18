package org.sar.ppi;

public abstract class Infrastructure {

    protected Protocol protocol;

    
    public Infrastructure(Protocol protocol) {
        this.protocol = protocol;
    }
    
    public abstract void run(String[] args);
    public abstract void send(Node dest, Object message);
}