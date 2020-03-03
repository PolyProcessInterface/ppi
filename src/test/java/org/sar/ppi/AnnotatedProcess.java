package org.sar.ppi;

/**
 * ExampleNodeProces
 */
public class AnnotatedProcess extends NodeProcess {

	public static class ExampleMessage extends Message{
	
		private static final long serialVersionUID = 1L;
		private String s;
		public ExampleMessage(int src, int dest, String s) {
			super(src, dest);
			this.s = s;
		}

		public String getS() {
			return s;
		}

	}

	@MessageHandler(msgClass=ExampleMessage.class)
	public void processExampleMessage(ExampleMessage message) {
		int host = infra.getId();
		System.out.println("" + host + " Received hello from " + message.getIdsrc());
		if (host != 0) {
			int dest = (host + 1) % infra.size();
			infra.send(new ExampleMessage(infra.getId(), dest, "hello"));
		}
		infra.exit();
	}

	@Override
	public void start() {
		if (infra.getId() == 0) {
			infra.send(new ExampleMessage(infra.getId(), 1, "hello"));
		}else {
		}
	}

	@Override
	public Object clone() {
		return new AnnotatedProcess();
	}
}
