package com.re.paas.integrated.fusion.functionalities;

import com.re.paas.api.fusion.functionalities.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.functionalities.Functionality;

public enum RoleFunctionalities implements Functionality {

	GET_ROLE_REALMS(-1, "list_role_realms"), 
	LIST_ROLES(-2, "list_role_names"),
	
	GET_REALM_FUNCTIONALITIES(1, "get_realm_functionalities", false),
	GET_ROLE_FUNCTIONALITIES(2, "get_role_functionalities", false),
	
	MANAGE_ROLES(3, "manage_roles");
	
	private static final String NAMESPACE = "roles";

	public static class Constants {
		private static final String PREFIX = NAMESPACE + AbstractFunctionalityDelegate.NAMESPACE_DELIMETER;

		public static final String GET_ROLE_REALMS = PREFIX + -1;
		public static final String LIST_ROLES = PREFIX + -2;
		public static final String GET_REALM_FUNCTIONALITIES = PREFIX + 1;
		public static final String GET_ROLE_FUNCTIONALITIES = PREFIX + 2;
		public static final String MANAGE_ROLES = PREFIX + 3;
	}

	private final int id;
	private final String name;
	private final Boolean isVisible;

	private final boolean isFrontend = true;
	private final boolean isBackend = true;

	private RoleFunctionalities(int id, String name) {
		this(id, name, true);
	}

	private RoleFunctionalities(int id, String name, boolean isVisible) {
		this.id = id;
		this.name = name;
		this.isVisible = isVisible;
	}

	public static RoleFunctionalities from(Integer value) {

		switch (value) {

		case -1:
			return RoleFunctionalities.GET_ROLE_REALMS;
			
		case -2:
			return RoleFunctionalities.LIST_ROLES;
			
		case 1:
			return RoleFunctionalities.GET_REALM_FUNCTIONALITIES;
			
		case 2:
			return RoleFunctionalities.GET_ROLE_FUNCTIONALITIES;
			
		case 3:
			return RoleFunctionalities.MANAGE_ROLES;

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
