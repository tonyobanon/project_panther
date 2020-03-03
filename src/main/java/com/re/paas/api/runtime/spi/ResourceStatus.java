package com.re.paas.api.runtime.spi;

public enum ResourceStatus {

	UPDATED, NOT_UPDATED, ERROR;

	private String message;
	private Integer count = 1;

	public String getMessage() {
		return message;
	}

	public ResourceStatus setMessage(String message) {
		this.message = message;
		return this;
	}

	public Integer getCount() {
		return count;
	}

	public ResourceStatus setCount(Integer count) {
		this.count = count;
		return this;
	}
}
