package org.sar.ppi.simulator;


import org.sar.ppi.Infrastructure;

import java.util.List;

public class ProtocolExecutor {
    private Infrastructure infra;

    public ProtocolExecutor(Infrastructure infra_user){
        infra=infra_user;
    }

    /**
     *
     * @param path
     * path to the JSON file with list of function to be called.
     */
    public void simulate(String path){
        List<Object[]> l_call = ProtocolTools.readProtocolJSON(path);
    }


}