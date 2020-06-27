package org.sar.ppi.dispatch;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.sar.ppi.NodeProcess;
import org.sar.ppi.events.Message;

public class Dispatcher {
	protected Map<Class<? extends Message>, Method> handlers;

	public Dispatcher(NodeProcess process) {
		this.handlers = extractHandlers(process);
	}

	protected Map<Class<? extends Message>, Method> extractHandlers(NodeProcess process) {
		Map<Class<? extends Message>, Method> handlers = new HashMap<>();
		Class<?>[] params;
		Class<? extends Message> param;
		for (Method m : process.getClass().getDeclaredMethods()) {
			if (!m.isAnnotationPresent(MessageHandler.class)) {
				continue;
			}
			params = m.getParameterTypes();
			if (params.length != 1) {
				throw new DispatcherException(m.getName() + ": should have a single parameter");
			}
			if (!Message.class.isAssignableFrom(params[0])) {
				throw new DispatcherException(m.getName() + ": param must extend Message");
			}
			param = params[0].asSubclass(Message.class);
			if (handlers.containsKey(param)) {
				throw new DispatcherException("More than one handler for: " + param.getName());
			}
			handlers.put(param, m);
		}
		return handlers;
	}

	/**
	 * Get the correct method to call for this message.
	 *
	 * @param message the message to process.
	 * @return the correct method to process this message.
	 * @throws NoSuchMethodException if there is no such method.
	 */
	public Method methodFor(Message message) throws NoSuchMethodException {
		if (!handlers.containsKey(message.getClass())) {
			throw new NoSuchMethodException();
		}
		return handlers.get(message.getClass());
	}
}
