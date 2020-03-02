package org.sar.ppi;

/**
 * ExampleNodeProces
 */
public class ExampleNodeProcess extends NodeProcess {

	@Override
	public void processMessage(int src, Object message) {
		int host = infra.getId();
		System.out.println("" + host + " Received hello from "  + src);
		if (host != 0) {
			infra.send((host + 1) % infra.size(), 0);
		}
		infra.exit();
	}

	@Override
	public void start() {
		if (infra.getId() == 0) {
			//System.err.println("SENDING FIRST MESSAGE");
			infra.send(1,0);
		}else {
			//System.err.println("NOT SENDING FIRST MESSAGE BECAUSE ID ==   "+infra.getId());
		}
	}

	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return new ExampleNodeProcess();
	}
}
