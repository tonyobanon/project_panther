package com.re.paas.internal.fusion.functionalities;

import com.re.paas.api.fusion.services.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.services.Functionality;

public enum SearchFunctionalities implements Functionality {

	PERFORM_LIST_OPERATION(-1, "perform_list_operation"),
	GET_SEARCHABLE_LISTS(1, "get_searchable_lists");
	
	private static final String NAMESPACE = "search";
	
	public static class Constants {

		private static final String PREFIX = NAMESPACE + AbstractFunctionalityDelegate.NAMESPACE_DELIMETER;

		public static final String PERFORM_LIST_OPERATION = PREFIX + -1;
		public static final String GET_SEARCHABLE_LISTS = PREFIX + 1;
	}

	private final int id;
	private final String name;
	private final Boolean isVisible;

	private final boolean isFrontend = true;
	private final boolean isBackend = true;

	private SearchFunctionalities(int id, String name) {
		this(id, name, true);
	}

	private SearchFunctionalities(int id, String name, boolean isVisible) {
		this.id = id;
		this.name = name;
		this.isVisible = isVisible;
	}

	public static SearchFunctionalities from(Integer value) {

		switch (value) {

		case -1:
			return PERFORM_LIST_OPERATION;
			
		case 1:
			return GET_SEARCHABLE_LISTS;

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
