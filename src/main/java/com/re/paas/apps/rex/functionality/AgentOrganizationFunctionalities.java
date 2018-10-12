package com.re.paas.apps.rex.functionality;

import com.re.paas.api.fusion.services.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.services.Functionality;

public enum AgentOrganizationFunctionalities implements Functionality {

	LIST_AGENT_ORGANIZATION_NAMES(-1, "list_agent_organization_names"),

	VIEW_AGENT_ORGANIZATION(-2, "view_agent_organization"),
	SEARCH_AGENT_ORGANIZATION(-3, "search_agent_organizations"),

	CREATE_AGENT_ORGANIZATION_MESSAGES(-4, "create_agent_organization_messages"),
	CREATE_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGES(-5, "create_agent_organization_whistleblow_messages"),

	CREATE_AGENT_ORGANIZATION_REVIEW(-6, "create_agent_organzation_review"),
	VIEW_AGENT_ORGANIZATION_REVIEWS(-7, "view_agent_organization_reviews"),

	LIST_AGENT_ORGANIZATION(-8, "list_agent_organizations"),
	
	
	UPDATE_AGENT_ORGANIZATION(3, "update_agent_organization"),
	DELETE_AGENT_ORGANIZATION(4, "delete_agent_organization"),

	LIST_AGENT_ORGANIZATION_MESSAGES(5, "list_agent_organization_messages"),
	UPDATE_AGENT_ORGANIZATION_MESSAGES(6, "update_agent_organization_messages"),
	DELETE_AGENT_ORGANIZATION_MESSAGES(7, "delete_agent_organization_messages"),
	VIEW_AGENT_ORGANIZATION_MESSAGE(8, "view_agent_organization_message"),

	LIST_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGES(9, "list_agent_organization_whistleblow_messages"),
	UPDATE_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGES(10, "update_agent_organization_whistleblow_messages"),
	DELETE_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGES(11, "delete_agent_organization_whistleblow_messages"),
	VIEW_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGE(12, "view_agent_organization_whistleblow_message"),

	DELETE_AGENT_ORGANIZATION_REVIEWS(13, "delete_agent_organization_reviews"),

	UPDATE_AGENT_ORGANIZATION_AVAILABILITY_SCHEDULE(14, "update_agent_organization_availability_schedule"),
	GET_AGENT_ORGANIZATION_AVAILABILITY_SCHEDULES(15, "get_agent_organization_availability_schedules"),

	VIEW_ORGANIZATION_ADMIN_APPLICATIONS(16, "view_organization_admin_applications"),
	REVIEW_ORGANIZATION_ADMIN_APPLICATION(17, "review_organization_admin_application");
	
	private static final String NAMESPACE = "agent_organization";

	public static class Constants {

		private static final String PREFIX = NAMESPACE + AbstractFunctionalityDelegate.NAMESPACE_DELIMETER;

		public static final String LIST_AGENT_ORGANIZATION_NAMES = PREFIX + -1;
		public static final String VIEW_AGENT_ORGANIZATION = PREFIX + -2;
		public static final String SEARCH_AGENT_ORGANIZATION = PREFIX + -3;
		public static final String CREATE_AGENT_ORGANIZATION_MESSAGES = PREFIX + -4;
		public static final String CREATE_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGES = PREFIX + -5;
		public static final String CREATE_AGENT_ORGANIZATION_REVIEW = PREFIX + -6;
		public static final String VIEW_AGENT_ORGANIZATION_REVIEWS = PREFIX + -7;
		public static final String LIST_AGENT_ORGANIZATION = PREFIX + -8;
		
		public static final String UPDATE_AGENT_ORGANIZATION = PREFIX + 3;
		public static final String DELETE_AGENT_ORGANIZATION = PREFIX + 4;
		public static final String LIST_AGENT_ORGANIZATION_MESSAGES = PREFIX + 5;
		public static final String UPDATE_AGENT_ORGANIZATION_MESSAGES = PREFIX + 6;
		public static final String DELETE_AGENT_ORGANIZATION_MESSAGES = PREFIX + 7;
		public static final String VIEW_AGENT_ORGANIZATION_MESSAGE = PREFIX + 8;
		public static final String LIST_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGES = PREFIX + 9;
		public static final String UPDATE_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGES = PREFIX + 10;
		public static final String DELETE_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGES = PREFIX + 11;
		public static final String VIEW_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGE = PREFIX + 12;
		public static final String DELETE_AGENT_ORGANIZATION_REVIEWS = PREFIX + 13;
		public static final String UPDATE_AGENT_ORGANIZATION_AVAILABILITY_SCHEDULE = PREFIX + 14;
		public static final String GET_AGENT_ORGANIZATION_AVAILABILITY_SCHEDULES = PREFIX + 15;
		public static final String VIEW_ORGANIZATION_ADMIN_APPLICATIONS = PREFIX + 16;
		public static final String REVIEW_ORGANIZATION_ADMIN_APPLICATION = PREFIX + 17;

	}

	private final int id;
	private final String name;
	private final Boolean isVisible;

	private final boolean isFrontend = true;
	private final boolean isBackend = true;

	private AgentOrganizationFunctionalities(int id, String name) {
		this(id, name, true);
	}

	private AgentOrganizationFunctionalities(int id, String name, boolean isVisible) {
		this.id = id;
		this.name = name;
		this.isVisible = isVisible;
	}

	public static AgentOrganizationFunctionalities from(Integer value) {

		switch (value) {

		case -1:
			return LIST_AGENT_ORGANIZATION_NAMES;

		case -2:
			return VIEW_AGENT_ORGANIZATION;

		case -3:
			return SEARCH_AGENT_ORGANIZATION;

		case -4:
			return CREATE_AGENT_ORGANIZATION_MESSAGES;

		case -5:
			return CREATE_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGES;

		case -6:
			return CREATE_AGENT_ORGANIZATION_REVIEW;

		case -7:
			return VIEW_AGENT_ORGANIZATION_REVIEWS;
			
		case -8:
			return LIST_AGENT_ORGANIZATION;
			

		case 3:
			return UPDATE_AGENT_ORGANIZATION;

		case 4:
			return DELETE_AGENT_ORGANIZATION;

		case 5:
			return LIST_AGENT_ORGANIZATION_MESSAGES;

		case 6:
			return UPDATE_AGENT_ORGANIZATION_MESSAGES;

		case 7:
			return DELETE_AGENT_ORGANIZATION_MESSAGES;

		case 8:
			return VIEW_AGENT_ORGANIZATION_MESSAGE;

		case 9:
			return LIST_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGES;

		case 10:
			return UPDATE_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGES;

		case 11:
			return DELETE_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGES;

		case 12:
			return VIEW_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGE;

		case 13:
			return DELETE_AGENT_ORGANIZATION_REVIEWS;

		case 14:
			return UPDATE_AGENT_ORGANIZATION_AVAILABILITY_SCHEDULE;

		case 15:
			return GET_AGENT_ORGANIZATION_AVAILABILITY_SCHEDULES;
			
		case 16:
			return VIEW_ORGANIZATION_ADMIN_APPLICATIONS;

		case 17:
			return REVIEW_ORGANIZATION_ADMIN_APPLICATION;

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
