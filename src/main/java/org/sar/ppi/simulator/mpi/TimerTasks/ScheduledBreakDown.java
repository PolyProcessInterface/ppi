package org.sar.ppi.simulator.mpi.TimerTasks;

import org.sar.ppi.NodeProcess;

import java.io.Serializable;
import java.util.TimerTask;

public class ScheduledBreakDown extends TimerTask implements Serializable {
    private NodeProcess node;
    public ScheduledBreakDown(NodeProcess node){
        this.node=node;
    }
    @Override
    public void run() {
        node.setIs_down(true);
    }
}
