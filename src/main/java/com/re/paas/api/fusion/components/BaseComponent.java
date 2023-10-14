package com.re.paas.api.fusion.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.utils.Utils;

public abstract class BaseComponent<T extends BaseComponent<T>> extends RecordNode<T> {

	private final String id;
	private final WebClientConnector delegate;

	private final Map<String, Object> config = new HashMap<>();

	private String sessionId;

	private List<CompletableFuture<?>> futures = new ArrayList<>();
	private List<EventListenerHandle<? extends T>> eventListeners = new ArrayList<>();

	protected BaseComponent() {
		this.id = this.getClass().getSimpleName() + "_" + Utils.newRandom();
		this.delegate = WebClientConnector.get();
	}

	WebClientConnector getDelegate() {
		return delegate;
	}

	public String getId() {
		return id;
	}

	public String getSessionId() {
		return sessionId;
	}
	
	public abstract String getAssetId();


	public void setSessionId(String sessionId) {
		if (this.sessionId == null) {
			this.sessionId = sessionId;

			this.delegate.addComponentToSession(sessionId, this);

		} else {
			Exceptions.throwRuntime(getId() + " is already connected to a client session");
		}
	}
	
	void addFuture(CompletableFuture<?> future) {
		futures.add(future);
	}

	public void await() {
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
		futures.clear();
	}
	
	public void render(String sessionId) {
		// TODO
	}

	protected void invokeBehaviour(BehaviorInvokeHandle handle) {
		if (getSessionId() != null) {
			addFuture(this.delegate.invokeBehaviorInClient((BehaviorInvokeHandle) handle.setComponent(this)));
		}
	}

	protected void dispatchEvent(EventDispatchHandle handle) {
		if (getSessionId() != null) {
			addFuture(this.delegate.dispatchEventToClient((EventDispatchHandle) handle.setComponent(this)));
		}
	}

	protected <U extends T> EventListenerHandle<U> addEventListener(EventListenerHandle<U> handle) {
		eventListeners.add(handle);
		return handle;
	}

	public void addConfig(String name, Object value) {
		config.put(name, value);
	}

	public Map<String, Object> getConfig() {
		return config;
	}
}
