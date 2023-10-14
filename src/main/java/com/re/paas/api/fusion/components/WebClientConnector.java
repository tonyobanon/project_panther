package com.re.paas.api.fusion.components;

import java.util.concurrent.CompletableFuture;

import com.re.paas.api.Singleton;

public interface WebClientConnector {
	
	<T extends BaseComponent<T>> void addComponentToSession(String sessionId, BaseComponent<T> component);
	
	<T> CompletableFuture<Void> setFieldValueInClient(FieldSetHandle<T> handle);
	
	CompletableFuture<Void> invokeBehaviorInClient(BehaviorInvokeHandle handle);

	CompletableFuture<Void> dispatchEventToClient(EventDispatchHandle handle);
	
	public static WebClientConnector get() {
		return Singleton.get(WebClientConnector.class);
	}
}
