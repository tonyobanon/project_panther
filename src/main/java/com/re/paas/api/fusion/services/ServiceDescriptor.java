package com.re.paas.api.fusion.services;

import com.re.paas.api.fusion.Endpoint;

public class ServiceDescriptor {

	private final String className;
	private final String methodName;
	private final Endpoint endpoint;

	
	public ServiceDescriptor(String className, String methodName, Endpoint endpoint) {
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

	public Endpoint getEndpoint() {
		return endpoint;
	}
	
	@Override
	public String toString() {
		return className + "#" + methodName;
	}
}
