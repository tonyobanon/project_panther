package com.re.paas.integrated.fusion.functionalities;

import com.re.paas.api.fusion.functionalities.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.functionalities.Functionality;

public enum BlobFunctionalities implements Functionality {

	GET_BINARY_DATA(-1, "get_binary_data"),
	SAVE_BINARY_DATA(-2, "save_binary_data"),
	MANAGE_BINARY_DATA(1, "manage_binary_data");
	
	private static final String NAMESPACE = "blob";
	
	public static class Constants {
		
		private static final String PREFIX = NAMESPACE + 
				AbstractFunctionalityDelegate.NAMESPACE_DELIMETER;
		
		public static final String GET_BINARY_DATA = PREFIX + -1;
		public static final String SAVE_BINARY_DATA = PREFIX + -2;
		public static final String MANAGE_BINARY_DATA = PREFIX + 1;
	}

	private final int id;
	private final String name;
	private final Boolean isVisible;

	private final boolean isFrontend = true;
	private final boolean isBackend = true;

	private BlobFunctionalities(int id, String name) {
		this(id, name, true);
	}

	private BlobFunctionalities(int id, String name, boolean isVisible) {
		this.id = id;
		this.name = name;
		this.isVisible = isVisible;
	}

	public static BlobFunctionalities from(Integer value) {

		switch (value) {

		case -1:
			return GET_BINARY_DATA;
		case -2:
			return SAVE_BINARY_DATA;
		case 1:
			return MANAGE_BINARY_DATA;

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
