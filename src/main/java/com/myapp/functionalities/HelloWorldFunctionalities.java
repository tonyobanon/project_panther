package com.myapp.functionalities;

import com.re.paas.api.fusion.functionalities.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.functionalities.Functionality;

public enum HelloWorldFunctionalities implements Functionality {

	SAY_HELLO(-1, "say_hello");
	
	private static final String NAMESPACE = "blob";
	
	public static class Constants {
		
		private static final String PREFIX = NAMESPACE + 
				AbstractFunctionalityDelegate.NAMESPACE_DELIMETER;
		
		public static final String SAY_HELLO = PREFIX + -1;
	}

	private final int id;
	private final String name;
	private final Boolean isVisible;

	private final boolean isFrontend = true;
	private final boolean isBackend = true;

	private HelloWorldFunctionalities(int id, String name) {
		this(id, name, true);
	}

	private HelloWorldFunctionalities(int id, String name, boolean isVisible) {
		this.id = id;
		this.name = name;
		this.isVisible = isVisible;
	}

	public static HelloWorldFunctionalities from(Integer value) {

		switch (value) {

		case -1:
			return SAY_HELLO;

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
