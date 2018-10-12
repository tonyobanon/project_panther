package com.re.paas.internal.fusion.services.impl;

import java.lang.reflect.Method;

import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.FusionEndpoint;

public class FusionServiceContext {

	private final BaseService service;
	private final FusionEndpoint endpoint;
	private final Method method;
	private final boolean isClassEnd;
	
	FusionServiceContext(BaseService service, FusionEndpoint endpoint, Method method, boolean isClassEnd) {
		this.service = service;
		this.endpoint = endpoint;
		this.method = method;
		this.isClassEnd = isClassEnd;
	}

	public BaseService getService() {
		return service;
	}

	public FusionEndpoint getEndpoint() {
		return endpoint;
	}

	public Method getMethod() {
		return method;
	}

	public boolean isClassEnd() {
		return isClassEnd;
	}
	
}
