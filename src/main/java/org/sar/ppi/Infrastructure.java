package org.sar.ppi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sar.ppi.communication.Message;
import org.sar.ppi.events.Call;
import org.sar.ppi.events.Deploy;
import org.sar.ppi.events.Event;
import org.sar.ppi.events.Undeploy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;

/**
 * Abstract Infrastructure class.
 */
public abstract class Infrastructure {
	private static final Logger logger = LogManager.getLogger();

	protected NodeProcess process;
	protected int currentNode;
	protected static final Lock lock = new ReentrantLock();
	protected Thread mainThread;
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
	public NodeProcess getProcess() { return process; }

	/**
	 * Process an event.
	 *
	 * Infrastructure implementations should call this method for each
	 * event to process.
	 * @param event
	 */
	protected void processEvent(Event event) {
		if (event instanceof Message) {
			serialThreadRun(() -> {
				this.process.processMessage((Message) event);
			});
		} else if (event instanceof Deploy) {
			deploy();
		} else if (event instanceof Undeploy) {
			undeploy();
		} else if (event instanceof Call) {
			Call call = (Call) event;
			Method m;
			try {
				m = process.getClass().getMethod(call.getFunction(), call.argsClasses());
				serialThreadRun(() -> {
					try {
						m.invoke(getProcess(), call.getArgs());
					} catch (IllegalAccessException | InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Run a new thread that will start immediately. The current thread
	 * will wait for it to end or to wait before continuing its process.
	 *
	 * @param method a {@link java.lang.Runnable} object.
	 */
	public void serialThreadRun(Runnable method) {
		Thread t = new Thread(() -> {
			synchronized (lock) {
				logger.debug("{} Start thread {}", getId(), Thread.currentThread().getId());
				runningThreads.add(Thread.currentThread());
				method.run();
				nextThread = mainThread;
				lock.notifyAll();
				runningThreads.remove(Thread.currentThread());
				logger.debug("{} Terminated thread {}", getId(), Thread.currentThread().getId());
			}
		});
		synchronized (lock) {
			mainThread = Thread.currentThread();
			nextThread = t;
			t.start();
			while (Thread.currentThread() != nextThread) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt(); // preserve interruption status
					t.interrupt();
					break;
				}
			}
			tryJoinThread(t);
			serialThreadScheduler();
		}
	}

	/**
	 * This scheduler iterates over the waiting serialThreads and wakes them up if
	 * they can continue.
	 */
	protected void serialThreadScheduler() {
		synchronized (lock) {
			for (BooleanSupplier condition : threads.keySet()) {
				if (condition.getAsBoolean()) {
					Thread thread = threads.get(condition);
					nextThread = thread;
					lock.notifyAll();
					while (Thread.currentThread() != nextThread) {
						try {
							lock.wait();
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
				logger.debug("{} Try joining terminated thread {}", getId(), thread.getId());
				thread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // preserve interruption status
				logger.debug("{} Could not join terminated thread {}", getId(), thread.getId());
				return false;
			}
			logger.debug("{} Succedded joining terminated thread {}", getId(), thread.getId());
			return true;
		}
		return false;
	}

	/**
	 * Wait until the condition is true. This function can not be used in
	 * {@link org.sar.ppi.NodeProcess#init(String[])}.
	 *
	 * @param condition a lambda which returns a boolean.
	 * @throws java.lang.InterruptedException if the process has been interrupted while waiting.
	 */
	public void wait(BooleanSupplier condition) throws InterruptedException {
		synchronized (lock) {
			if (!condition.getAsBoolean()) {
				threads.put(condition, Thread.currentThread());
				logger.info("{} Start waiting on {}", this.getId(), condition);
				nextThread = mainThread;
				while (Thread.currentThread() != nextThread) {
					lock.notifyAll();
					try {
						lock.wait();
					} catch (InterruptedException e) {
						threads.remove(condition);
						throw new InterruptedException();
					}
				}
				logger.info("{} Stopped waiting", this.getId());
				threads.remove(condition);
			}
		}
	}
}
