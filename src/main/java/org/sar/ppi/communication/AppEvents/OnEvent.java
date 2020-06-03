package org.sar.ppi.communication.AppEvents;

import org.sar.ppi.peersim.PeerSimInfrastructure;

public class OnEvent implements AppEvent {
    private PeerSimInfrastructure peerInfra;

    public OnEvent(PeerSimInfrastructure p){peerInfra=p;}

    @Override
    public void run() {
        peerInfra.deploy();
    }
}
