package com.re.paas.api.fusion.components;

public class BehaviorInvokeHandle extends Handle {
	
	private final String name;
	private final Object[] args;
	
	public BehaviorInvokeHandle(String name, Object... args) {
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
