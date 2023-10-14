package com.re.paas.api.fusion.components;

public class EventDispatchHandle extends Handle {
	
	private final String name;
	private final Object[] args;
	
	public EventDispatchHandle(String name, Object... args) {
		this.name = name;
		this.args = args;
	}
	
	String getName() {
		return name;
	}
	
	Object[] getArgs() {
		return args;
	}
}
