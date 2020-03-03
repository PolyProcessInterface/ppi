package org.sar.ppi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Process
 */
public abstract class NodeProcess {

	protected Infrastructure infra;

	public void setInfra(Infrastructure infra) {
		this.infra = infra;
	}

	/**
	 * Handler to process a received message.
	 *
	 * @param message the message received.
	 */
	public void processMessage(Message message) {
		Method[] methods = this.getClass().getMethods();
		for (Method method : methods) {
			MessageHandler annot = method.getAnnotation(MessageHandler.class);
			if (annot == null)
				continue;
			if (message.getClass().equals(annot.msgClass())) {
				try {
					method.invoke(this, message);
				} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Start execution sequence for the current node.
	 */
	public abstract void start();
	
	/**
	 * Needed for peersim
	 */
	public abstract Object clone();
}