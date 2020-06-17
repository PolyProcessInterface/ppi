package org.sar.ppi.events;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class Call extends ScheduledEvent {
	private static final long serialVersionUID = 1L;
	
	@JsonPropertyDescription("The function to call")
	protected String function;
	@JsonPropertyDescription("The arguments to pass to the function")
	protected Object[] args;

	public void setFunction(String function) {
		this.function = function;
	}

	public String getFunction() {
		return function;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Object[] getArgs() {
		return args;
	}

	public Class<?>[] argsClasses() {
		Class<?>[] classes = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			classes[i] = args[i].getClass();
		}
		return classes;
	}

	@Override
	public String toString() {
		String args = Arrays.toString(this.args);
		return "call(" +super.toString() + ", function:" + function + ", args:" + args + ")";
	}
}
