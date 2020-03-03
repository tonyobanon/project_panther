package com.re.paas.integrated.fusion.functionalities;

import com.re.paas.api.fusion.functionalities.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.functionalities.Functionality;

public enum UserFunctionalities implements Functionality {

	AUTHENTICATE(-1, "authenticate", false), 
	VIEW_OWN_PROFILE(1, "view_own_profile", false), 
	MANAGE_OWN_PROFILE(2, "manage_own_profile", false),
	GET_PERSON_NAMES(3, "get_person_names", false),
	GET_USER_PROFILE(4, "get_user_profile"), 
	MANAGE_USER_ACCOUNTS(5, "manage_user_accounts");
	
	private static final String NAMESPACE = "user";

	public static class Constants {

		private static final String PREFIX = NAMESPACE + AbstractFunctionalityDelegate.NAMESPACE_DELIMETER;

		public static final String AUTHENTICATE = PREFIX + -1;
		
		public static final String VIEW_OWN_PROFILE = PREFIX + 1;
		public static final String MANAGE_OWN_PROFILE = PREFIX + 2;

		public static final String GET_PERSON_NAMES = PREFIX + 3;
		public static final String GET_USER_PROFILE = PREFIX + 4;
		public static final String MANAGE_USER_ACCOUNTS = PREFIX + 5;
	}

	private final int id;
	private final String name;
	private final Boolean isVisible;

	private final boolean isFrontend = true;
	private final boolean isBackend = true;

	private UserFunctionalities(int id, String name) {
		this(id, name, true);
	}

	private UserFunctionalities(int id, String name, boolean isVisible) {
		this.id = id;
		this.name = name;
		this.isVisible = isVisible;
	}

	public static UserFunctionalities from(Integer value) {

		switch (value) {
		
		case -1:
			return AUTHENTICATE;

		case 1:
			return VIEW_OWN_PROFILE;

		case 2:
			return MANAGE_OWN_PROFILE;

		case 3:
			return GET_PERSON_NAMES;

		case 4:
			return GET_USER_PROFILE;

		case 5:
			return MANAGE_USER_ACCOUNTS;

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
