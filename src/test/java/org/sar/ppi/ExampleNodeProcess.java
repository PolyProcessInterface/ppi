package org.sar.ppi;

/**
 * ExampleNodeProces
 */
public class ExampleNodeProcess extends NodeProcess {

	@Override
	public void processMessage(Message message) {
		long hostid = message.getIddest();
		System.out.println("" + hostid + " Received hello from "  + message.getIdsrc());
		if (hostid != 0) {
			infra.send(new ObjectMessage(message.getIddest() , message.getIddest()+1%infra.size() , 0 , null));
		}
		infra.exit();
	}

	@Override
	public void start() {
		if (infra.getId() == 0) {
			infra.send(new ObjectMessage(0 , 1 , 0 , null));
		}
	}

	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return null;
	}
}