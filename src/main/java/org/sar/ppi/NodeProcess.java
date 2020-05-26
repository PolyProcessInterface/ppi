package org.sar.ppi;

import org.sar.ppi.communication.AppMessage.AppMessage;
import org.sar.ppi.communication.AppMessage.SchedMessage;
import org.sar.ppi.communication.AppMessage.ShedBreakMessage;
import org.sar.ppi.communication.AppMessage.ShedOnMessage;
import org.sar.ppi.communication.Message;
import org.sar.ppi.communication.MessageHandler;
import org.sar.ppi.communication.MessageHandlerException;
import org.sar.ppi.communication.Tasks.SchedDeploy;
import org.sar.ppi.communication.Tasks.ScheduledBreakDown;
import org.sar.ppi.communication.Tasks.ScheduledFunction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Node Process Abstract class.
 */
public abstract class NodeProcess {

	protected Infrastructure infra;
	protected Timer timer;

	protected AtomicBoolean is_down = new AtomicBoolean(false);
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
		//System.err.println("Starting to process a message from " + message.getIdsrc() + " to " + message.getIddest());
		if(message instanceof AppMessage) {
			processAppMessage(message);
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
			try {
				method.invoke(this, message);
				return;
			} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}

	private void processAppMessage(Message message) {
		//mpi
		if(message instanceof SchedMessage && !is_down.get()) {
			SchedMessage shed = (SchedMessage) message;
			timer.schedule(new ScheduledFunction(shed.getName(),shed.getArgs(),this,this.infra),shed.getDelay());
		}
		if(message instanceof ShedBreakMessage)
			timer.schedule(new ScheduledBreakDown(this),((ShedBreakMessage) message).getDelay());

		if(message instanceof ShedOnMessage)
			timer.schedule(new SchedDeploy(this.infra),((ShedOnMessage) message).getDelay());
	}

	/**
	 * Start execution sequence for the current node.
	 */
	public abstract void start();
	
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

	public void stopSched(){
		if(timer!=null) {
			timer.cancel();
			timer=null;
		}
	}

	/**
	 * Getter for the field <code>timer</code>.
	 *
	 * @return a {@link java.util.Timer} object.
	 */
	public Timer getTimer() {
		if(timer==null){
			timer=new Timer(true);
			return timer;
		}
		return timer;
	}

	public void setIs_down(boolean val) {

		/*if(val){
			stopSched();
			timer =  new Timer(true);
		}*/
		System.out.println("Is down = "+val);
		is_down.set(val);
	}

	public boolean getIs_down() { return is_down.get(); }

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "NodeProcess{" + "infra=" + infra.getId() + '}';
	}
}
