package com.re.paas.api.infra.cache;

public class CacheFactoryStats {

	private int exponent;
	private int totalUsed;
	private int totalFree;
	private long clientCount;
	private long maxConnectionsAllowed;
	private long maxConnections;


	public int getExponent() {
		return exponent;
	}

	public CacheFactoryStats setExponent(int exponent) {
		this.exponent = exponent;
		return this;
	}
	
	public int getTotalUsed() {
		return totalUsed;
	}

	public CacheFactoryStats setTotalUsed(int totalUsed) {
		this.totalUsed = totalUsed;
		return this;
	}

	public int getTotalFree() {
		return totalFree;
	}

	public CacheFactoryStats setTotalFree(int totalFree) {
		this.totalFree = totalFree;
		return this;
	}

	public long getClientCount() {
		return clientCount;
	}

	public CacheFactoryStats setClientCount(long clientCount) {
		this.clientCount = clientCount;
		return this;
	}

	public long getMaxConnectionsAllowed() {
		return maxConnectionsAllowed;
	}

	public CacheFactoryStats setMaxConnectionsAllowed(long maxConnectionsAllowed) {
		this.maxConnectionsAllowed = maxConnectionsAllowed;
		return this;
	}

	public long getMaxConnections() {
		return maxConnections;
	}

	public CacheFactoryStats setMaxConnections(long maxConnections) {
		this.maxConnections = maxConnections;
		return this;
	}

}
