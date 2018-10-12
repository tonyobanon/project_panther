package com.re.paas.internal.models.errors;

import com.re.paas.api.errors.Error;

public enum RexError implements Error {

	AGENT_ORGANIZATION_MISMATCH(5,
			"There is an organization mismatch, verify that you belong to the same organization"),

	THE_SPECIFIED_DATE_IS_A_HOLIDAY(10, "The specified date is a public holiday");

	private boolean isFatal;
	private int code;
	private String message;

	private RexError(Integer code, String message) {
		this(code, message, false);
	}

	private RexError(Integer code, String message, boolean isFatal) {
		this.code = code;
		this.message = message;
		this.isFatal = isFatal;
	}

	@Override
	public String namespace() {
		return "rex";
	}

	public static RexError from(int value) {

		switch (value) {

		case 5:
			return RexError.AGENT_ORGANIZATION_MISMATCH;

		case 10:
			return RexError.THE_SPECIFIED_DATE_IS_A_HOLIDAY;

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
