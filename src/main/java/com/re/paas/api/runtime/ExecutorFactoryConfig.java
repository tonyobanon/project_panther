package com.re.paas.api.runtime;

public class ExecutorFactoryConfig {
	
	private int maxThreads;
	
	public ExecutorFactoryConfig(int maxThreads) {
		super();
		this.maxThreads = maxThreads;
	}

	public int getMaxThreads() {
		return maxThreads;
	}
	
	public ExecutorFactoryConfig setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
		return this;
	}
}
