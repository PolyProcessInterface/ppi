package org.sar.ppi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;

/**
 * Abstract Infrastructure class.
 */
public abstract class Infrastructure {

	protected NodeProcess process;
	protected int currentNode;
	protected static final Lock lock = new ReentrantLock();
	protected Thread mainThread;
	protected Thread nextThread;
	protected Map<BooleanSupplier, Thread> threads = new ConcurrentHashMap<>();

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
	 * Run a new thread that will start immediately. The current thread
	 * will wait for it to end or to wait before continuing its process.
	 *
	 * @param method a {@link java.lang.Runnable} object.
	 */
	public void serialThreadRun(Runnable method) {
		Thread t = new Thread(() -> {
			synchronized (lock) {
				method.run();
				nextThread = mainThread;
				lock.notifyAll();
			}
		});
		synchronized (lock) {
			mainThread = Thread.currentThread();
			nextThread = t;
			t.start();
			while (Thread.currentThread() != nextThread) {
				try {
					lock.wait();
					serialThreadScheduler();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * This scheduler iterates over the waiting serialThreads and wakes them up
	 * if they can continue.
	 */
	protected void serialThreadScheduler() {
		synchronized (lock) {
			for (BooleanSupplier condition : threads.keySet()) {
				if (condition.getAsBoolean()) {
					nextThread = threads.get(condition);
					lock.notifyAll();
					while (Thread.currentThread() != nextThread) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * Wait until the condition is true. This function can not be used in
	 * {@link org.sar.ppi.NodeProcess#start()}.
	 *
	 * @param condition a lambda which returns a boolean.
	 * @throws java.lang.InterruptedException if the process has been interrupted while waiting.
	 */
	public void wait(BooleanSupplier condition) throws InterruptedException {
		synchronized (lock) {
			if (!condition.getAsBoolean()) {
				threads.put(condition, Thread.currentThread());
				System.out.printf("%d Start waiting on %s\n", this.getId(), condition.toString());
				nextThread = mainThread;
				while (Thread.currentThread() != nextThread) {
					lock.notifyAll();
					lock.wait();
				}
				System.out.printf("%d Stopped waiting\n", this.getId());
				threads.remove(condition);
			}
		}
	}
}
