package org.sar.ppi.simulator.peersim.AppEvents;

import org.sar.ppi.peersim.PeerSimInfrastructure;

public class OnEvent implements AppEvent {
    private PeerSimInfrastructure peerInfra;

    public OnEvent(PeerSimInfrastructure p){peerInfra=p;}

    @Override
    public void run() {
        peerInfra.deploy();
    }
}
