package com.re.paas.internal.roles;

import com.re.paas.api.events.BaseEvent;

public class RoleInitEvent extends BaseEvent {

	private static final long serialVersionUID = 1L;

	private final String roleName;
	private final boolean succeeded;
	
	private String message;
	
	public RoleInitEvent(String roleName, Boolean succeeded) {
		this.roleName = roleName;
		this.succeeded = succeeded;
	}

	public boolean isSucceeded() {
		return succeeded;
	}
	
	public String getRoleName() {
		return roleName;
	}
	
	public String getMessage() {
		return message;
	}

	public RoleInitEvent setMessage(String message) {
		this.message = message;
		return this;
	}
}
