package com.re.paas.internal.fusion.functionalities;

import com.re.paas.api.fusion.services.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.services.Functionality;

public enum AuthFunctionalities implements Functionality {

	EMAIL_LOGIN_USER(-1, "email_user_login"), 
	PHONE_LOGIN_USER(-2, "phone_user_login");

	private static final String NAMESPACE = "auth";

	public static class Constants {
		
		private static final String PREFIX = NAMESPACE + 
				AbstractFunctionalityDelegate.NAMESPACE_DELIMETER;
		
		public static final String EMAIL_LOGIN_USER = PREFIX + -1;
		public static final String PHONE_LOGIN_USER = PREFIX + -2;
	}

	private final int id;
	private final String name;
	private final Boolean isVisible;

	private final boolean isFrontend = true;
	private final boolean isBackend = true;

	private AuthFunctionalities(int id, String name) {
		this(id, name, true);
	}

	private AuthFunctionalities(int id, String name, boolean isVisible) {
		this.id = id;
		this.name = name;
		this.isVisible = isVisible;
	}

	public static AuthFunctionalities from(Integer value) {

		switch (value) {

		case -1:
			return EMAIL_LOGIN_USER;
		case -2:
			return PHONE_LOGIN_USER;

		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public final int id() {
		return id;
	}

	@Override
	public Boolean isVisible() {
		return isVisible;
	}

	@Override
	public final String namespace() {
		return NAMESPACE;
	}

	@Override
	public Boolean isFrontend() {
		return isFrontend;
	}

	@Override
	public Boolean isBackend() {
		return isBackend;
	}
	
}
