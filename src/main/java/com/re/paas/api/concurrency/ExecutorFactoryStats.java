package com.re.paas.api.concurrency;

public class ExecutorFactoryStats {

	private int exponent;
	private int totalUsed;
	private int totalFree;
	private long threadCount;
	private long maxThreads;

	
	
	public int getExponent() {
		return exponent;
	}

	public ExecutorFactoryStats setExponent(int exponent) {
		this.exponent = exponent;
		return this;
	}

	public int getTotalUsed() {
		return totalUsed;
	}

	public ExecutorFactoryStats setTotalUsed(int totalUsed) {
		this.totalUsed = totalUsed;
		return this;
	}

	public int getTotalFree() {
		return totalFree;
	}

	public ExecutorFactoryStats setTotalFree(int totalFree) {
		this.totalFree = totalFree;
		return this;
	}

	public long getThreadCount() {
		return threadCount;
	}

	public ExecutorFactoryStats setThreadCount(long threadCount) {
		this.threadCount = threadCount;
		return this;
	}

	public long getMaxThreads() {
		return maxThreads;
	}

	public ExecutorFactoryStats setMaxThreads(long maxThreads) {
		this.maxThreads = maxThreads;
		return this;
	}

}
