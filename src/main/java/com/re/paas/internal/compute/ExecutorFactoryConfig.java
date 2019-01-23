package com.re.paas.internal.compute;

public class ExecutorFactoryConfig {
	
	private int maxThreads;
	private boolean isTrusted;
	
	
	public ExecutorFactoryConfig(int maxThreads, boolean isTrusted) {
		super();
		this.maxThreads = maxThreads;
		this.isTrusted = isTrusted;
	}

	public int getMaxThreads() {
		return maxThreads;
	}
	
	public ExecutorFactoryConfig setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
		return this;
	}
	
	public boolean isTrusted() {
		return isTrusted;
	}
	
	public ExecutorFactoryConfig setTrusted(boolean isTrusted) {
		this.isTrusted = isTrusted;
		return this;
	}
	
}
