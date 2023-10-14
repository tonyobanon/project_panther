
package com.re.paas.api.fusion.components;

import java.util.function.Consumer;

public class EventListenerHandle<T extends BaseComponent<? super T>> extends Handle {

	private final String name;
	private final FunctionalInterface listener;
	private boolean once;
	
	public EventListenerHandle(String name, Consumer<Object[]> listener) {
		this.name = name;
		this.listener = (FunctionalInterface) listener;
	}
	
	public EventListenerHandle(String name, Function listener) {
		this.name = name;
		this.listener = (FunctionalInterface) listener;
	}

	FunctionalInterface getListener() {
		return this.listener;
	}

	String getName() {
		return name;
	}
	
	boolean isOnce() {
		return once;
	}

	@SuppressWarnings("unchecked")
	public <U extends T> U once() {
		this.once = true;
		return (U) getComponent();
	}
	
	@SuppressWarnings("unchecked")
	public <U extends T> U every() {
		this.once = false;
		return (U) getComponent();
	}
}
