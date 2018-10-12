package com.re.paas.internal.fusion.functionalities;

import com.re.paas.api.fusion.services.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.services.Functionality;

public enum TaskFunctionalities implements Functionality {

	EXECUTE_JOBS(-1, "execute_jobs"),

	CREATE_TASK(1, "new_task"), 
	GET_IMAGE(2, "get_image"),
	VIEW_JOBS(3, "view_jobs"), 
	DELETE_TASK(4, "delete_task");

	private static final String NAMESPACE = "task";
	
	public static class Constants {
		
		private static final String PREFIX = NAMESPACE + AbstractFunctionalityDelegate.NAMESPACE_DELIMETER;

		public static final String EXECUTE_JOBS = PREFIX + -1;
		public static final String CREATE_TASK = PREFIX + 1;
		public static final String GET_IMAGE = PREFIX + 2;
		public static final String VIEW_JOBS = PREFIX + 3;
		public static final String DELETE_TASK = PREFIX + 4;
	}

	private final int id;
	private final String name;
	private final Boolean isVisible;

	private final boolean isFrontend = true;
	private final boolean isBackend = true;

	private TaskFunctionalities(int id, String name) {
		this(id, name, true);
	}

	private TaskFunctionalities(int id, String name, boolean isVisible) {
		this.id = id;
		this.name = name;
		this.isVisible = isVisible;
	}

	public static TaskFunctionalities from(Integer value) {

		switch (value) {

		case -1:
			return EXECUTE_JOBS;

		case 1:
			return CREATE_TASK;

		case 2:
			return GET_IMAGE;

		case 3:
			return VIEW_JOBS;

		case 4:
			return DELETE_TASK;

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
