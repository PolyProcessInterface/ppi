package org.sar.ppi;

/**
 * Protocol
 */
public abstract class Protocol {

    protected Infrastructure infrastructure;

    public abstract void processMessage(Node src, Object message);
}