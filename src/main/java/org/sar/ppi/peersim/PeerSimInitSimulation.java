package org.sar.ppi.peersim;

import org.sar.ppi.PpiException;
import org.sar.ppi.simulator.ProtocolTools;
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

    private String FileName=System.getProperty("user.dir")+"/testeJson.json";;
    public PeerSimInitSimulation(String prefix) {
        infrapid=Configuration.getPid(prefix+"."+PAR_PROTO);
    }

    @Override
    public boolean execute() {
        if(FileName==null)
            throw new PpiException("File name not set");

        launchSimulation(FileName);
        for(int i = 0; i < Network.size(); i++) {
            Node node=Network.get(i);
            PeerSimInfrastructure pInfra = (PeerSimInfrastructure) node.getProtocol(infrapid);
            pInfra.initialize(node);
        }
        System.out.println("Node Init for Node =");

        return false;
    }

    private void launchSimulation(String path) {
        List<Object[]> l_call = ProtocolTools.readProtocolJSON(path);
        int num_node;
        long delay;
        for(Object[] func : l_call){
            num_node=(int)func[1];
            delay=(long)func[2];
            EDSimulator.add(delay,new SchedEvent((String) func[0], Arrays.copyOfRange(func,3,func.length)),Network.get(num_node),num_node);
        }
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }
}
