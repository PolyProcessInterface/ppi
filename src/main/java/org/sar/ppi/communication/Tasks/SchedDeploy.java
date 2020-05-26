package org.sar.ppi.communication.Tasks;

import org.sar.ppi.Infrastructure;
import org.sar.ppi.mpi.MpiInfrastructure;

import java.io.Serializable;
import java.util.TimerTask;

public class SchedDeploy extends TimerTask implements Serializable {

    private MpiInfrastructure mp;
    public SchedDeploy(Infrastructure mp){ this.mp=  (MpiInfrastructure)mp;}
    @Override
    public void run() {
        mp.deploy();
    }
}