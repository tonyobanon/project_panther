package com.re.paas.internal.clustering;

import com.re.paas.api.events.BaseEvent;

public class RoleStartCompleteEvent extends BaseEvent {

	private static final long serialVersionUID = 1L;

	private final boolean succeeded;
	private String message;
	
	public RoleStartCompleteEvent(boolean succeeded) {
		this.succeeded = succeeded;
	}

	public boolean isSucceeded() {
		return succeeded;
	}
	
	public String getMessage() {
		return message;
	}

	public RoleStartCompleteEvent setMessage(String message) {
		this.message = message;
		return this;
	}

	@Override
	public String name() {
		return "RoleStartCompleteEvent";
	}

}
