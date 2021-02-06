package org.sar.ppi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sar.ppi.dispatch.Dispatcher;
import org.sar.ppi.events.Message;

/**
 * Node Process Abstract class.
 */
public abstract class NodeProcess {
	private static final Logger LOGGER = LogManager.getLogger();

	protected Infrastructure infra;
	private Dispatcher dispatcher;
	private AtomicBoolean deployed = new AtomicBoolean(true);

	public NodeProcess() {
		this.dispatcher = new Dispatcher(this);
	}

	/**
	 * Setter for the field <code>infra</code>.
	 *
	 * @param infra a {@link org.sar.ppi.Infrastructure} object.
	 */
	public void setInfra(Infrastructure infra) {
		this.infra = infra;
	}

	/**
	 * Handler to process a received message.
	 *
	 * @param message the message received.
	 */
	public void processMessage(Message message) {
		try {
			Method method = dispatcher.methodFor(message);
			method.invoke(this, message);
		} catch (
			InvocationTargetException
			| IllegalAccessException
			| IllegalArgumentException
			| NoSuchMethodException e
		) {
			LOGGER.error("Could not process a message", e);
		}
	}

	/**
	 * Start init sequence for the current node.
	 *
	 * @param args arguments to pass to this node process.
	 */
	public abstract void init(String[] args);

	/**
	 * Needed for peersim. Return a new intance of the current class by default.
	 *
	 * @return a {@link java.lang.Object} object.
	 * @throws java.lang.CloneNotSupportedException if fail to instanciate a new instance.
	 */
	public Object clone() throws CloneNotSupportedException {
		try {
			return this.getClass().newInstance();
		} catch (ReflectiveOperationException e) {
			throw new CloneNotSupportedException();
		}
	}

	/**
	 * Deploy the current node so it can receive messages.
	 */
	void deploy() {
		deployed.set(true);
	}

	/**
	 * Undeploy the current node (turn it off).
	 */
	void undeploy() {
		deployed.set(false);
	}

	boolean isDeployed() {
		return deployed.get();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "NodeProcess{" + "infra=" + infra.getId() + '}';
	}
}
