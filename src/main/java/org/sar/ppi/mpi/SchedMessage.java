package org.sar.ppi.mpi;

import org.sar.ppi.Message;

public class SchedMessage extends Message {
    private Object[] args;
    private String name ;
    private long delay;
    public SchedMessage(int idsrc, int iddest , String name ,long delay, Object[] args) {
        super(idsrc, iddest);
        this.name=name;
        this.args=args;
        this.delay=delay;
    }

    public Object[] getArgs() {
        return args;
    }

    public String getName() {
        return name;
    }

    public long getDelay() {
        return delay;
    }
}
