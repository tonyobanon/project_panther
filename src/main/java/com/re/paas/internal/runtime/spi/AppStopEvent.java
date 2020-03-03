package com.re.paas.internal.runtime.spi;

import com.re.paas.api.events.BaseEvent;

public class AppStopEvent extends BaseEvent {

	private static final long serialVersionUID = 1L;

	private final String appId;
	private final boolean success;
	private Object reason;

	public AppStopEvent(String appId, boolean success) {
		super();
		this.appId = appId;
		this.success = success;
	}

	public Object getReason() {
		return reason;
	}

	public AppStopEvent setReason(Object reason) {
		this.reason = reason;
		return this;
	}

	public String getAppId() {
		return appId;
	}
	
	public boolean isSuccess() {
		return success;
	}
}
