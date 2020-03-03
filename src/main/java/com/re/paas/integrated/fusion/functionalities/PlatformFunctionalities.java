package com.re.paas.integrated.fusion.functionalities;

import com.re.paas.api.fusion.functionalities.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.functionalities.Functionality;

public enum PlatformFunctionalities implements Functionality {

	PLATFORM_INSTALLATION(-1, "install_platform"),

	ADD_SYSTEM_MOCK_DATA(1, "add_system_mock_data", false),

	MANAGE_ACTIVITY_STREAM(2, "manage_activity_stream"),
	MANAGE_SYSTEM_CONFIGURATION_FORM(3, "manage_system_configuration_form"),

	MANAGE_SYSTEM_CACHES(4, "manage_system_caches"),

	VIEW_SYSTEM_CONFIGURATION(5, "view_system_configuration"),
	UPDATE_SYSTEM_CONFIGURATION(6, "update_system_configuration"),

	VIEW_SYSTEM_MTERICS(7, "view_system_metrics");

	private static final String NAMESPACE = "platform";

	public static class Constants {

		private static final String PREFIX = NAMESPACE + AbstractFunctionalityDelegate.NAMESPACE_DELIMETER;

		public static final String PLATFORM_INSTALLATION = PREFIX + -1;
		public static final String ADD_SYSTEM_MOCK_DATA = PREFIX + 1;
		public static final String MANAGE_ACTIVITY_STREAM = PREFIX + 2;
		public static final String MANAGE_SYSTEM_CONFIGURATION_FORM = PREFIX + 3;
		public static final String MANAGE_SYSTEM_CACHES = PREFIX + 4;
		public static final String VIEW_SYSTEM_CONFIGURATION = PREFIX + 5;
		public static final String UPDATE_SYSTEM_CONFIGURATION = PREFIX + 6;
		public static final String VIEW_SYSTEM_MTERICS = PREFIX + 7;

	}

	private final int id;
	private final String name;
	private final Boolean isVisible;

	private final boolean isFrontend = true;
	private final boolean isBackend = true;

	private PlatformFunctionalities(int id, String name) {
		this(id, name, true);
	}

	private PlatformFunctionalities(int id, String name, boolean isVisible) {
		this.id = id;
		this.name = name;
		this.isVisible = isVisible;
	}

	public static PlatformFunctionalities from(Integer value) {

		switch (value) {

		case -1:
			return PLATFORM_INSTALLATION;

		case 1:
			return ADD_SYSTEM_MOCK_DATA;
		case 2:
			return MANAGE_ACTIVITY_STREAM;
		case 3:
			return MANAGE_SYSTEM_CONFIGURATION_FORM;
		case 4:
			return MANAGE_SYSTEM_CACHES;
		case 5:
			return VIEW_SYSTEM_CONFIGURATION;
		case 6:
			return UPDATE_SYSTEM_CONFIGURATION;
		case 7:
			return VIEW_SYSTEM_MTERICS;

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
