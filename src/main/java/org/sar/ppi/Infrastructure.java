package org.sar.ppi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;


public abstract class Infrastructure {

	protected NodeProcess process;
	protected int currentNode;
	protected static final Lock lock = new ReentrantLock();
	Thread nextThread;
	Map<BooleanSupplier, Thread> threads = new ConcurrentHashMap<>();

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
	 * @return the currentNode
	 */
	public int getId() {
		return currentNode;
	}

	/**
	 * Stop the execution of the infrastructure for th current node.
	 */
	public abstract void exit();

	/**
	 * Return the number of nodes in the infrastructure.
	 *
	 * @return number of nodes.
	 */
	public abstract int size();

	public void serialThreadRun(Runnable method) {
		Thread t = new Thread(() -> {
			synchronized (lock) {
				method.run();
				lock.notifyAll();
			}
		});
		synchronized (lock) {
			nextThread = Thread.currentThread();
			t.start();
			try {
				lock.wait();
				serialThreadScheduler();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected void serialThreadScheduler() {
		synchronized (lock) {
			for (BooleanSupplier condition : threads.keySet()) {
				if (condition.getAsBoolean()) {
					nextThread = threads.get(condition);
					lock.notifyAll();
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

	public void wait(BooleanSupplier condition) throws InterruptedException {
		synchronized(lock) {
			if (!condition.getAsBoolean()) {
				threads.put(condition, Thread.currentThread());
				System.out.printf("%d Start waiting on %s\n", this.getId(), condition.toString());
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