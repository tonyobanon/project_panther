package com.re.paas.internal.fusion.components;

import java.util.concurrent.CompletableFuture;

import com.re.paas.api.fusion.components.BaseComponent;
import com.re.paas.api.fusion.components.BehaviorInvokeHandle;
import com.re.paas.api.fusion.components.WebClientConnector;
import com.re.paas.api.fusion.components.EventDispatchHandle;
import com.re.paas.api.fusion.components.FieldSetHandle;

public class WebClientConnectorImpl implements WebClientConnector {

	// TODO Use the data grid to store data
	
	@Override
	public <T extends BaseComponent<T>> void addComponentToSession(String sessionId, BaseComponent<T> component) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public <T> CompletableFuture<Void> setFieldValueInClient(FieldSetHandle<T> handle) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public CompletableFuture<Void> invokeBehaviorInClient(BehaviorInvokeHandle handle) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public CompletableFuture<Void> dispatchEventToClient(EventDispatchHandle handle) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
