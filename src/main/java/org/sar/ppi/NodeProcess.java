package org.sar.ppi;

import org.sar.ppi.mpi.SchedMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;

/**
 * Process
 */
public abstract class NodeProcess {

	protected Infrastructure infra;
	protected static Lock lock = new ReentrantLock();
	protected static Lock lock_pre = new ReentrantLock();
	private static List<BooleanSupplier> conditions = new ArrayList<>();
	
	protected Timer timer = new Timer();
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
		if(message instanceof SchedMessage) {
			SchedMessage shed = (SchedMessage) message;
			timer.schedule(new ScheduledFunction(shed.getName(),shed.getArgs(),this),shed.getDelay());
			return;
		}
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
					e.printStackTrace();
				}
			}
			
			synchronized (lock_pre) {
				for (BooleanSupplier condition : conditions) {
					if (condition.getAsBoolean()) {
						lock_pre.notifyAll();
					}
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

	public void stopSched(){
		if(timer!=null) {
			timer.cancel();
			timer=null;
		}
	}

	public Timer getTimer() {
		return timer;
	}

	@Override
	public String toString() {
		return "NodeProcess{" + "infra=" + infra.getId() + '}';
	}
	
	
	/*public <T extends NodeProcess> void waiting(Predicate<T> predicate) { // Predicate<? extends NodeProcess>
		synchronized (lock) {
			while(!predicate.test((T) this)) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}*/

	/**
	 * wait until the condition becomes true.
	 * @param condition
	 */
	public <T extends NodeProcess> void waiting(BooleanSupplier condition) {
		synchronized (lock_pre) {
			conditions.add(condition);
			while(! condition.getAsBoolean()) {
				try {
					lock_pre.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			conditions.remove(condition);
		}
	}

}