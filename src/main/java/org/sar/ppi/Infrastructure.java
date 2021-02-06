package org.sar.ppi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sar.ppi.events.Call;
import org.sar.ppi.events.Deploy;
import org.sar.ppi.events.Event;
import org.sar.ppi.events.Message;
import org.sar.ppi.events.ScheduledEvent;
import org.sar.ppi.events.Timeout;
import org.sar.ppi.events.Undeploy;

/**
 * Abstract Infrastructure class.
 */
public abstract class Infrastructure {
	private static final Logger LOGGER = LogManager.getLogger();

	protected NodeProcess process;
	protected int currentNode;
	protected static final Lock LOCK = new ReentrantLock();
	protected Stack<Thread> prevThreads = new Stack<>();
	protected Thread nextThread;
	protected Map<BooleanSupplier, Thread> threads = new ConcurrentHashMap<>();
	protected Set<Thread> runningThreads = new HashSet<>();

	/**
	 * Constructor for Infrastructure.
	 *
	 * @param process a {@link org.sar.ppi.NodeProcess} object.
	 */
	public Infrastructure(NodeProcess process) {
		this.process = process;
	}

	/**
	 * Send a message to node.
	 *
	 * @param message the message to send.
	 */
	public abstract void send(Message message);

	/**
	 * Get the current node's id.
	 *
	 * @return current node's id.
	 */
	public int getId() {
		return currentNode;
	}

	/**
	 * Stop the execution of the infrastructure for the current node.
	 */
	public abstract void exit();

	/**
	 * Return the number of nodes in the infrastructure.
	 *
	 * @return number of nodes.
	 */
	public abstract int size();

	/**
	 * Get current time.
	 *
	 * The unit will depend on the infrastructure.
	 *
	 * @return current time.
	 */
	public abstract long currentTime();

	/**
	 * Schedule a call to trigger after a delay.
	 */
	public void scheduleCall(String function, Object[] args, int delay) {
		Call call = new Call();
		call.setNode(getId());
		call.setFunction(function);
		call.setArgs(args);
		call.setDelay(delay);
		scheduleEvent(call);
	}

	/**
	 * Internally schedule a call.
	 */
	protected abstract void scheduleEvent(ScheduledEvent event);

	/**
	 * Deploy the current node so it can receive messages.
	 */
	protected void deploy() {
		process.deploy();
	}

	/**
	 * Undeploy the current node (turn it off).
	 */
	protected void undeploy() {
		process.undeploy();
	}

	protected boolean isDeployed() {
		return process.isDeployed();
	}

	/**
	 *
	 * @return
	 * the current process linked to this infra
	 */
	public NodeProcess getProcess() {
		return process;
	}

	/**
	 * Process an event.
	 *
	 * Infrastructure implementations should call this method for each
	 * event to process.
	 * @param event
	 */
	protected void processEvent(Event event) {
		if (event instanceof Message) {
			serialThreadRun(
				() -> {
					this.process.processMessage((Message) event);
				}
			);
		} else if (event instanceof Deploy) {
			deploy();
		} else if (event instanceof Undeploy) {
			undeploy();
		} else if (event instanceof Timeout) {
			Timeout timout = (Timeout) event;
			for (Thread t : Thread.getAllStackTraces().keySet()) {
				if (t.getId() == timout.getThreadId()) {
					t.interrupt();
				}
			}
		} else if (event instanceof Call) {
			Call call = (Call) event;
			Method m;
			try {
				m = process.getClass().getMethod(call.getFunction(), call.argsClasses());
				serialThreadRun(
					() -> {
						try {
							m.invoke(getProcess(), call.getArgs());
						} catch (IllegalAccessException | InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				);
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Run a new thread that will start immediately. The current thread will wait
	 * for it to end or to wait before continuing its process.
	 *
	 * @param method a {@link java.lang.Runnable} object.
	 * @return the created thread;
	 */
	public Thread serialThreadRun(Runnable method) {
		Thread t = new Thread(
			() -> {
				synchronized (LOCK) {
					LOGGER.debug("{} Start thread {}", getId(), Thread.currentThread().getId());
					runningThreads.add(Thread.currentThread());
					method.run();
					nextThread = prevThreads.pop();
					LOCK.notifyAll();
					runningThreads.remove(Thread.currentThread());
					LOGGER.debug(
						"{} Terminated thread {}",
						getId(),
						Thread.currentThread().getId()
					);
				}
			}
		);
		synchronized (LOCK) {
			prevThreads.push(Thread.currentThread());
			nextThread = t;
			t.start();
			while (Thread.currentThread() != nextThread) {
				try {
					LOCK.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt(); // preserve interruption status
					t.interrupt();
					break;
				}
			}
			tryJoinThread(t);
			serialThreadScheduler();
		}
		return t;
	}

	/**
	 * This scheduler iterates over the waiting serialThreads and wakes them up if
	 * they can continue.
	 */
	protected void serialThreadScheduler() {
		synchronized (LOCK) {
			for (BooleanSupplier condition : threads.keySet()) {
				if (condition.getAsBoolean()) {
					Thread thread = threads.get(condition);
					nextThread = thread;
					prevThreads.push(Thread.currentThread());
					LOCK.notifyAll();
					while (Thread.currentThread() != nextThread) {
						try {
							LOCK.wait();
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt(); // preserve interruption status
							return;
						}
					}
					tryJoinThread(thread);
				}
			}
		}
	}

	protected boolean tryJoinThread(Thread thread) {
		if (!runningThreads.contains(thread)) {
			try {
				LOGGER.debug("{} Try joining terminated thread {}", getId(), thread.getId());
				thread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // preserve interruption status
				LOGGER.debug("{} Could not join terminated thread {}", getId(), thread.getId());
				return false;
			}
			LOGGER.debug("{} Succedded joining terminated thread {}", getId(), thread.getId());
			return true;
		}
		return false;
	}

	/**
	 * Wait until the condition is true. This function can not be used in
	 * {@link org.sar.ppi.NodeProcess#init(String[])}, because it is not run in a serialThread yet.
	 *
	 * @param condition a lambda which returns a boolean.
	 * @throws java.lang.InterruptedException if the process has been interrupted while waiting.
	 */
	public void wait(BooleanSupplier condition) throws InterruptedException {
		waitFor(condition, 0);
	}

	/**
	 * Wait until the condition is true or the timout is reached. This function can not be used in
	 * {@link org.sar.ppi.NodeProcess#init(String[])}, because it is not run in a serialThread yet.
	 *
	 * @param condition a lambda which returns a boolean.
	 * @param timout duration until the wait should abort. (ignored if <= 0)
	 * @return true if the condition is fulfilled, false if it was aborted because of the timeout.
	 * @throws java.lang.InterruptedException if the process has been interrupted while waiting.
	 */
	public boolean waitFor(BooleanSupplier condition, long timeout) throws InterruptedException {
		if (timeout > 0) {
			Timeout event = new Timeout();
			event.setNode(getId());
			event.setDelay(timeout);
			event.setThreadId(Thread.currentThread().getId());
			scheduleEvent(event);
		}
		synchronized (LOCK) {
			if (!condition.getAsBoolean()) {
				threads.put(condition, Thread.currentThread());
				LOGGER.info("{} Start waiting on {}", this.getId(), condition);
				nextThread = prevThreads.pop();
				while (Thread.currentThread() != nextThread) {
					LOCK.notifyAll();
					try {
						LOCK.wait();
					} catch (InterruptedException e) {
						threads.remove(condition);
						if (!condition.getAsBoolean() && timeout >= currentTime()) {
							return false;
						}
						throw new InterruptedException();
					}
				}
				LOGGER.info("{} Stopped waiting", this.getId());
				threads.remove(condition);
			}
		}
		return true;
	}
}
