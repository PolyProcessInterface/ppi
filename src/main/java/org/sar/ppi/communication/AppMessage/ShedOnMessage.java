package org.sar.ppi.communication.AppMessage;

import org.sar.ppi.communication.Message;

public class ShedOnMessage extends Message implements AppMessage {
    /**
     * Version
     */
    private static final long serialVersionUID = 1L;
    private long delay;

    public ShedOnMessage(int idsrc, int iddest, long delay) {
        super(idsrc, iddest);
        this.delay = delay;
    }

    public long getDelay() {
        return delay;
    }
}