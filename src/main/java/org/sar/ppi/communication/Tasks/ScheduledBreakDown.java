package org.sar.ppi.communication.Tasks;

import org.sar.ppi.NodeProcess;

import java.io.Serializable;
import java.util.TimerTask;

public class ScheduledBreakDown extends TimerTask implements Serializable {
    /**
     * Version
     */
    private static final long serialVersionUID = 1L;

    private NodeProcess node;
    public ScheduledBreakDown(NodeProcess node){
        this.node=node;
    }
    @Override
    public void run() {
        node.setIs_down(true);
    }
}
