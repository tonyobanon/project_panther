package com.re.paas.api.runtime.spi;

public class DistributedStoreConfig {

	private final String name;
	private Boolean alwaysReturnValue;
	private Boolean persist;

	public DistributedStoreConfig(String name) {
		this.name = name;
		this.alwaysReturnValue = true;
		this.persist = false;
	}

	public String getName() {
		return name;
	}

	public Boolean getAlwaysReturnValue() {
		return alwaysReturnValue;
	}

	public boolean isPersist() {
		return persist;
	}

	public DistributedStoreConfig setAlwaysReturnValue(Boolean alwaysReturnValue) {
		this.alwaysReturnValue = alwaysReturnValue;
		return this;
	}

	public DistributedStoreConfig setPersist(Boolean persist) {
		this.persist = persist;
		return this;
	}

}
