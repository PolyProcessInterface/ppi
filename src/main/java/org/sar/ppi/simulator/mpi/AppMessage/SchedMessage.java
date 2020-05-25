package org.sar.ppi.simulator.mpi.AppMessage;

import org.sar.ppi.Message;

/**
 * SchedMessage class.
 */
public class SchedMessage extends Message {

    private static final long serialVersionUID = 1L;

    private Object[] args;
    private String name ;
    private long delay;

    /**
     * Constructor for SchedMessage.
     *
     * @param idsrc a int.
     * @param iddest a int.
     * @param name a {@link java.lang.String} object.
     * @param delay a long.
     * @param args an array of {@link java.lang.Object} objects.
     */
    public SchedMessage(int idsrc, int iddest , String name ,long delay, Object[] args) {
        super(idsrc, iddest);
        this.name=name;
        this.args=args;
        this.delay=delay;
    }

    /**
     * Getter for the field <code>args</code>.
     *
     * @return an array of {@link java.lang.Object} objects.
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * Getter for the field <code>name</code>.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the field <code>delay</code>.
     *
     * @return a long.
     */
    public long getDelay() {
        return delay;
    }
}
