package org.sar.ppi;

/**
 * ExampleProtocol
 */
public class ExampleProtocol extends Protocol {

    @Override
    public void processMessage(Node src, Object message) {
        Node host = infra.getCurrentNode();
        if (host.getId() != 0) {
            infra.send(infra.getNode(host.getId() + 1 % infra.size()), 0);
        }
        infra.exit();
    }

    @Override
    public void startNode(Node node) {
        if (node.getId() == 0) {
            infra.send(infra.getNode(1), 0);
        }
    }
}