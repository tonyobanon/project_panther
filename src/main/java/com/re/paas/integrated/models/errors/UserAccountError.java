package com.re.paas.integrated.models.errors;

import com.re.paas.api.errors.Error;

public enum UserAccountError implements Error {
	
	PASSWORDS_MISMATCH(5, "Password mismatch"),
	EMAIL_ALREADY_EXISTS(10, "Email already exists"),
	PHONE_ALREADY_EXISTS(15, "Phone number already exists"),
	EMAIL_DOES_NOT_EXIST(20, "Email does not exist"),
	PHONE_DOES_NOT_EXIST(25, "Phone does not exist"),
	INCORRECT_PASSWORD(30, "Incorrect password");
	
	private boolean isFatal;
	private int code;
	private String message;

	private UserAccountError(Integer code, String message) {
		this(code, message, false);
	}
	
	private UserAccountError(Integer code, String message, boolean isFatal) {
		this.code = code;
		this.message = message;
		this.isFatal = isFatal;
	}

	public static UserAccountError from(int value) {

		switch (value) {
		
		case 5:
			return UserAccountError.PASSWORDS_MISMATCH;
		case 10:
			return UserAccountError.EMAIL_ALREADY_EXISTS;
		case 15:
			return UserAccountError.PHONE_ALREADY_EXISTS;
		case 20:
			return UserAccountError.EMAIL_DOES_NOT_EXIST;
		case 25:
			return UserAccountError.PHONE_DOES_NOT_EXIST;
		case 30:
			return UserAccountError.INCORRECT_PASSWORD;
		
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
		return "useracount";
	}
	
}
