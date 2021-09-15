package com.re.paas.internal.events.impl;

import java.lang.reflect.Method;

import com.re.paas.api.tasks.Affinity;

class Subscription {

	private final Object instance;
	private final Method method;
	private final boolean allowAsyncEvents;
	private final Affinity affinity;
	
	public Subscription(Object instance, Method method, boolean allowAsyncEvents, Affinity affinity) {
		this.instance = instance;
		this.method = method;
		this.allowAsyncEvents = allowAsyncEvents;
		this.affinity = affinity;
	}
	
	public Object getInstance() {
		return instance;
	}

	public Method getMethod() {
		return method;
	}
	
	public boolean isAllowAsyncEvents() {
		return allowAsyncEvents;
	}
	
	public Affinity getAffinity() {
		return affinity;
	}
}
