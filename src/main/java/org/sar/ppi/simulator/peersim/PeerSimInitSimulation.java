package org.sar.ppi.simulator.peersim;

import org.sar.ppi.PpiException;
import org.sar.ppi.peersim.PeerSimInfrastructure;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;


import java.util.Arrays;
import java.util.List;

public class PeerSimInitSimulation implements Control {
    private static final String PAR_PROTO="infrapid";
    private final int infrapid;
    private static final String TRANSPORT_SIMULATION="transport";
    private final int pid_trans;
    private String FileName;
    public PeerSimInitSimulation(String prefix) {
        infrapid=Configuration.getPid(prefix+"."+PAR_PROTO);
        pid_trans=Configuration.getPid(prefix+"."+TRANSPORT_SIMULATION);
        FileName=Configuration.getString("path");
    }

    @Override
    public boolean execute() {
        if(FileName==null)
            throw new PpiException("File name not set");
        for(int i = 0; i < Network.size(); i++) {
            Node node=Network.get(i);
            PeerSimInfrastructure pInfra = (PeerSimInfrastructure) node.getProtocol(infrapid);
            pInfra.initialize(node);
        }
        launchSimulation(FileName);

        return false;
    }

    private void launchSimulation(String path) {
        List<Object[]> l_call = ProtocolTools.readProtocolJSON(path);
        int num_node;
        long delay;
        for(Object[] func : l_call){
            num_node=(int)func[1];
            delay=(long)func[2];
            EDSimulator.add(delay,new SchedEvent((String) func[0], Arrays.copyOfRange(func,3,func.length)),Network.get(num_node),infrapid);
        }
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }
}
