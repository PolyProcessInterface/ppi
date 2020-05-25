package org.sar.ppi.simulator.mpi.AppMessage;

import org.sar.ppi.simulator.mpi.AppMessage.AppMessage;
import org.sar.ppi.Message;

public class ShedBreakMessage extends Message implements AppMessage {
    private long delay;
    public ShedBreakMessage(int idsrc, int iddest,long delay) {
        super(idsrc, iddest);
        this.delay=delay;
    }
    public long getDelay() {
        return delay;
    }

}
