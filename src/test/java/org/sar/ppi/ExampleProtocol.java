package org.sar.ppi;

/**
 * ExampleProtocol
 */
public class ExampleProtocol extends Protocol {

	@Override
	public void processMessage(Node src, Object message) {
		Node host = infra.getCurrentNode();
		System.out.println("" + host.getId() + " Rceived hello from "  + src.getId());
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
	// a changer
	public void setInfra(Infrastructure i){
		super.infra=i;
	}

	public static void main(String[] args) {
		ExampleProtocol e = new ExampleProtocol();
		MpiInfrastructure mpe = new MpiInfrastructure(e);
		e.setInfra(mpe);
		mpe.run(args);
		mpe.exit();
	}
}