package org.sar.ppi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Process
 */
public abstract class NodeProcess {

	protected Infrastructure infra;
	protected static Lock lock = new ReentrantLock();
	
	public void setInfra(Infrastructure infra) {
		this.infra = infra;
	}

	/**
	 * Handler to process a received message.
	 *
	 * @param message the message received.
	 */
	public void processMessage(Message message) {
		//System.err.println("Starting to process a message from " + message.getIdsrc() + " to " + message.getIddest());
		Method[] methods = this.getClass().getMethods();
		for (Method method : methods) {
			Class<?>[] params = method.getParameterTypes();
			if (!method.isAnnotationPresent(MessageHandler.class))
				continue;
			if (params.length != 1)
				throw new MessageHandlerException(method.getName() + ": should only have one parameter");
			if (!Message.class.isAssignableFrom(params[0]))
				throw new MessageHandlerException(method.getName() + ": first param must extend Message");
			if (!params[0].equals(message.getClass()))
				continue;
			Thread t = new Thread(() -> threadMessageHandler(method, message));
			synchronized (lock) {
				t.start();
				try {
					lock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	protected void threadMessageHandler(Method method, Message message)
	{
		try {
			synchronized (lock) {
				method.invoke(this, message);
				lock.notify();
			}
		} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Start execution sequence for the current node.
	 */
	public abstract void start();
	
	/**
	 * Needed for peersim. Return a new intance of the current class by default.
	 */
	public Object clone() throws CloneNotSupportedException {
		try {
			return this.getClass().newInstance();
		} catch (ReflectiveOperationException e) {
			throw new CloneNotSupportedException();
		}
	}

	@Override
	public String toString() {
		return "NodeProcess{" + "infra=" + infra.getId() + '}';
	}
}