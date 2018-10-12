package com.re.paas.internal.fusion.functionalities;

import com.re.paas.api.fusion.services.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.services.Functionality;

public enum FormFunctionalities implements Functionality {

	VIEW_APPLICATION_FORM(-1, "view_application_form"),
	GET_FORM_FIELDS(-2, "get_form_fields");
	
	private static final String NAMESPACE = "forms";
	
	public static class Constants {
		
		private static final String PREFIX = NAMESPACE + 
				AbstractFunctionalityDelegate.NAMESPACE_DELIMETER;
		
		public static final String VIEW_APPLICATION_FORM = PREFIX + -1;
		public static final String GET_FORM_FIELD_IDS = PREFIX + -2;
	}

	private final int id;
	private final String name;
	private final Boolean isVisible;

	private final boolean isFrontend = true;
	private final boolean isBackend = true;

	private FormFunctionalities(int id, String name) {
		this(id, name, true);
	}

	private FormFunctionalities(int id, String name, boolean isVisible) {
		this.id = id;
		this.name = name;
		this.isVisible = isVisible;
	}

	public static FormFunctionalities from(Integer value) {

		switch (value) {

		case -1:
			return VIEW_APPLICATION_FORM;
		case -2:
			return GET_FORM_FIELDS;
	
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
	
	@Override
	public final String alias() {
		return this.name();
	}
}
