package com.re.paas.apps.rex.functionality;

import com.re.paas.api.fusion.services.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.services.Functionality;

public enum AgentFunctionalities implements Functionality {

	VIEW_AGENT(-1, "view_agent"),
	DELETE_AGENT(1, "delete_agent"),

	UPDATE_AGENT_AVAILABILITY_SCHEDULE(2, "update_agent_availability_schedule"),
	GET_AGENT_AVAILABILITY_SCHEDULES(3, "get_agent_availability_schedules"),

	VIEW_AGENT_APPLICATIONS(4, "view_agent_applications"),
	REVIEW_AGENT_APPLICATION(5, "review_agent_application");
	
	private static final String NAMESPACE = "agent";
	
	public static class Constants {
		
		private static final String PREFIX = NAMESPACE + 
				AbstractFunctionalityDelegate.NAMESPACE_DELIMETER;
		
		public static final String VIEW_AGENT = PREFIX + -1;
		public static final String DELETE_AGENT = PREFIX + 1;
		
		public static final String UPDATE_AGENT_AVAILABILITY_SCHEDULE = PREFIX + 2;
		public static final String GET_AGENT_AVAILABILITY_SCHEDULES = PREFIX + 3;
	
		public static final String VIEW_AGENT_APPLICATIONS = PREFIX + 4;
		public static final String REVIEW_AGENT_APPLICATION = PREFIX + 5;

	}

	private final int id;
	private final String name;
	private final Boolean isVisible;

	private final boolean isFrontend = true;
	private final boolean isBackend = true;

	private AgentFunctionalities(int id, String name) {
		this(id, name, true);
	}

	private AgentFunctionalities(int id, String name, boolean isVisible) {
		this.id = id;
		this.name = name;
		this.isVisible = isVisible;
	}

	public static AgentFunctionalities from(Integer value) {

		switch (value) {

		case -1:
			return VIEW_AGENT;

		case 1:
			return DELETE_AGENT;
		
		case 2:
			return UPDATE_AGENT_AVAILABILITY_SCHEDULE;
		case 3:
			return GET_AGENT_AVAILABILITY_SCHEDULES;
			
		case 4:
			return VIEW_AGENT_APPLICATIONS;
		case 5:
			return REVIEW_AGENT_APPLICATION;
			
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
