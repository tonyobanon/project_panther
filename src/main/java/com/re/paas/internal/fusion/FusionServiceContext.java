package com.re.paas.internal.fusion;

import java.lang.reflect.Method;

import com.re.paas.api.fusion.Endpoint;
import com.re.paas.api.fusion.services.BaseService;

public class FusionServiceContext {

	private final BaseService service;
	private final Endpoint endpoint;
	private final Method method;
	private final boolean isClassEnd;
	
	FusionServiceContext(BaseService service, Endpoint endpoint, Method method, boolean isClassEnd) {
		this.service = service;
		this.endpoint = endpoint;
		this.method = method;
		this.isClassEnd = isClassEnd;
	}

	public BaseService getService() {
		return service;
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}

	public Method getMethod() {
		return method;
	}

	public boolean isClassEnd() {
		return isClassEnd;
	}
	
}
