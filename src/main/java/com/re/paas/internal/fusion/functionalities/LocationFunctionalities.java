package com.re.paas.internal.fusion.functionalities;

import com.re.paas.api.fusion.services.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.services.Functionality;

public enum LocationFunctionalities implements Functionality {

	GET_LOCATION_DATA(-1, "get_location_data"), 
	
	GET_COUNTRY_NAMES(-2, "get_country_names"),
	GET_TERRITORY_NAMES(-3, "get_territory_names"), 
	GET_CITY_NAMES(-4, "get_city_names"),
	
	GET_AVAILABLE_COUNTRIES(-5, "get_available_countries"),
	GET_RESOURCE_BUNDLE_ENTRIES(-6, "get_resource_bundle_entries");

	private static final String NAMESPACE = "location";
	
	public static class Constants {
		
		private static final String PREFIX = NAMESPACE + 
				AbstractFunctionalityDelegate.NAMESPACE_DELIMETER;
		
		public static final String GET_LOCATION_DATA = PREFIX + -1;
		public static final String GET_COUNTRY_NAMES = PREFIX + -2;
		public static final String GET_TERRITORY_NAMES = PREFIX + -3;
		public static final String GET_CITY_NAMES = PREFIX + -4;
		public static final String GET_AVAILABLE_COUNTRIES = PREFIX + -5;
		public static final String GET_RESOURCE_BUNDLE_ENTRIES = PREFIX + -6;
		
	}

	private final int id;
	private final String name;
	private final Boolean isVisible;

	private final boolean isFrontend = true;
	private final boolean isBackend = true;

	private LocationFunctionalities(int id, String name) {
		this(id, name, true);
	}

	private LocationFunctionalities(int id, String name, boolean isVisible) {
		this.id = id;
		this.name = name;
		this.isVisible = isVisible;
	}

	public static LocationFunctionalities from(Integer value) {

		switch (value) {

		case -1:
			return GET_LOCATION_DATA;
		case -2:
			return GET_COUNTRY_NAMES;
		case -3:
			return GET_TERRITORY_NAMES;
		case -4:
			return GET_CITY_NAMES;
		case -5:
			return GET_AVAILABLE_COUNTRIES;
		case -6:
			return GET_RESOURCE_BUNDLE_ENTRIES;

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
