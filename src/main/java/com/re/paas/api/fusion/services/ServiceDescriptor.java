package com.re.paas.api.fusion.services;

import com.re.paas.api.fusion.FusionEndpoint;

public class ServiceDescriptor {

	private final String className;
	private final String methodName;
	private final FusionEndpoint endpoint;

	
	public ServiceDescriptor(String className, String methodName, FusionEndpoint endpoint) {
		this.className = className;
		this.methodName = methodName;
		this.endpoint = endpoint;
	}

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName;
	}

	public FusionEndpoint getEndpoint() {
		return endpoint;
	}
}
