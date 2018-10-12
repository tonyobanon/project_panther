package com.re.paas.internal.errors;

import com.re.paas.api.errors.Error;

public enum NodeRoleError implements Error {

	UNKNOWN_ROLE_NAME_DEFINED(5, "Unknown role name: {ref1}");

	private boolean isFatal;
	private int code;
	private String message;

	private NodeRoleError(Integer code, String message) {
		this(code, message, false);
	}

	private NodeRoleError(Integer code, String message, boolean isFatal) {
		this.code = code;
		this.message = message;
		this.isFatal = isFatal;
	}

	@Override
	public String namespace() {
		return "rex";
	}

	public static NodeRoleError from(int value) {

		switch (value) {

		case 5:
			return NodeRoleError.UNKNOWN_ROLE_NAME_DEFINED;

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

}
