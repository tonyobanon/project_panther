package com.re.paas.internal.fusion.functionalities;

import com.re.paas.api.fusion.services.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.services.Functionality;

public enum SystemAdapterFunctionalities implements Functionality {

	GET_TYPES(1, "get_adapter_types"),
	GET_DESCRIPTIONS(2, "get_adapter_descriptions"),
	GET_PARAMETERS(3, "get_adapter_parameters"),
	CONFIGURE(4, "configure_adapter");
	
	private static final String NAMESPACE = "system_adapters";
	
	public static class Constants {
		
		private static final String PREFIX = NAMESPACE + 
				AbstractFunctionalityDelegate.NAMESPACE_DELIMETER;
		
		public static final String GET_TYPES = PREFIX + 1;
		public static final String GET_DESCRIPTIONS = PREFIX + 2;
		public static final String GET_PARAMETERS = PREFIX + 3;
		public static final String CONFIGURE = PREFIX + 4;
	}

	private final int id;
	private final String name;
	private final Boolean isVisible;

	private final boolean isFrontend = true;
	private final boolean isBackend = true;

	private SystemAdapterFunctionalities(int id, String name) {
		this(id, name, true);
	}

	private SystemAdapterFunctionalities(int id, String name, boolean isVisible) {
		this.id = id;
		this.name = name;
		this.isVisible = isVisible;
	}

	public static SystemAdapterFunctionalities from(Integer value) {

		switch (value) {

		case 1:
			return GET_TYPES;
			
		case 2:
			return GET_DESCRIPTIONS;
			
		case 3:
			return GET_PARAMETERS;
			
		case 4:
			return CONFIGURE;

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
