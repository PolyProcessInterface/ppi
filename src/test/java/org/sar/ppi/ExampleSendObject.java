package org.sar.ppi;

public class ExampleSendObject extends NodeProcess{
    @Override
    public void processMessage(int src, Object message) {
        int host = infra.getId();
        if (host != 0) {
            infra.send((host + 1) % infra.size(), "Send from "+host);
        }
        infra.exit();
    }

    @Override
    public void start() {
        if (infra.getId() == 0) {
            infra.send(1, " First send");
        }
    }
}
