package org.sar.ppi;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.sar.ppi.communication.Message;
import org.sar.ppi.communication.MessageHandler;
import org.sar.ppi.communication.MessageHandlerException;

public class MessageDispatcher {
	protected Map<Class<? extends Message>, Method> handlers;

	public MessageDispatcher(NodeProcess process) {
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
				throw new MessageHandlerException(m.getName() + ": should have a single parameter");
			}
			if (!Message.class.isAssignableFrom(params[0])) {
				throw new MessageHandlerException(m.getName() + ": param must extend Message");
			}
			param = params[0].asSubclass(Message.class);
			if (handlers.containsKey(param)) {
				throw new MessageHandlerException("More than one handler for: " + param.getName());
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
