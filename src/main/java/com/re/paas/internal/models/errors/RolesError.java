package com.re.paas.internal.models.errors;

import com.re.paas.api.errors.Error;

public enum RolesError implements Error {
	
	DEFAULT_ROLE_CANNOT_BE_DELETED(5, "Role is a default role cannot be deleted"),
	ROLE_IN_USE_AND_CANNOT_BE_DELETED(10, "Role is still in use, and therefore cannot be deleted");

	private boolean isFatal;
	private int code;
	private String message;

	private RolesError(Integer code, String message) {
		this(code, message, false);
	}
	
	private RolesError(Integer code, String message, boolean isFatal) {
		this.code = code;
		this.message = message;
		this.isFatal = isFatal;
	}

	public static RolesError from(int value) {

		switch (value) {
			
		case 5:
			return RolesError.DEFAULT_ROLE_CANNOT_BE_DELETED;
			
		case 10:
			return RolesError.ROLE_IN_USE_AND_CANNOT_BE_DELETED;
		
		default:
			return null;
			
		}
	}
	
	@Override
	public boolean isFatal() {
		return isFatal;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String namespace() {
		return "roles";
	}
	
}
