package com.re.paas.integrated.fusion.functionalities;

import com.re.paas.api.fusion.functionalities.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.functionalities.Functionality;

public enum UserApplicationFunctionalities implements Functionality {

	VIEW_APPLICATION_FORM(-1, "view_application_form"),
	
	CREATE_APPLICATION(-2, "create_new_application"), 
	DOWNLOAD_QUESTIONNAIRE(-3, "download_questionnaire"),

	UPDATE_APPLICATION(-4, "update_existing_application"), 
	SUBMIT_APPLICATION(-5, "submit_application"),

	CAN_USER_REVIEW_APPLICATION(-6, "", false),

	VIEW_ADMIN_APPLICATIONS(1, "view_admin_applications"),
	REVIEW_ADMIN_APPLICATION(2, "review_admin_application"),
	
	MANAGE_APPLICATION_FORMS(3, "manage_application_forms");

	private static final String NAMESPACE = "user_application";

	public static class Constants {
		
		private static final String PREFIX = NAMESPACE + AbstractFunctionalityDelegate.NAMESPACE_DELIMETER;

		public static final String VIEW_APPLICATION_FORM = PREFIX + -1;
		public static final String CREATE_APPLICATION = PREFIX + -2;
		public static final String DOWNLOAD_QUESTIONNAIRE = PREFIX + -3;
		public static final String UPDATE_APPLICATION = PREFIX + -4;
		public static final String SUBMIT_APPLICATION = PREFIX + -5;
		public static final String CAN_USER_REVIEW_APPLICATION = PREFIX + -6;
		
		public static final String VIEW_ADMIN_APPLICATIONS = PREFIX + 1;
		public static final String REVIEW_ADMIN_APPLICATION = PREFIX + 2;
		public static final String MANAGE_APPLICATION_FORMS = PREFIX + 3;
	}

	private final int id;
	private final String name;
	private final Boolean isVisible;

	private boolean isFrontend = true;
	private boolean isBackend = true;

	private UserApplicationFunctionalities(int id, String name) {
		this(id, name, true);
	}

	private UserApplicationFunctionalities(int id, String name, boolean isVisible) {
		this.id = id;
		this.name = name;
		this.isVisible = isVisible;
	}
	
	private UserApplicationFunctionalities(int id, String name, boolean isVisible, boolean isFrontend, boolean isBackend) {
		this(id, name, isVisible);
		this.isFrontend = isFrontend;
		this.isBackend = isBackend;
	}

	public static UserApplicationFunctionalities from(Integer value) {

		switch (value) {

		case -1:
			return VIEW_APPLICATION_FORM;
		case -2:
			return CREATE_APPLICATION;
		case -3:
			return DOWNLOAD_QUESTIONNAIRE;
		case -4:
			return UPDATE_APPLICATION;
		case -5:
			return SUBMIT_APPLICATION;
		case -6:
			return CAN_USER_REVIEW_APPLICATION;
			
		case 1:
			return VIEW_ADMIN_APPLICATIONS;
		case 2:
			return REVIEW_ADMIN_APPLICATION;
			
		case 3:
			return MANAGE_APPLICATION_FORMS;

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
