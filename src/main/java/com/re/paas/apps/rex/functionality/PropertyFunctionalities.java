package com.re.paas.apps.rex.functionality;

import com.re.paas.api.fusion.services.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.services.Functionality;

public enum PropertyFunctionalities implements Functionality {

	LIST_PROPERTY(-1, "list_property"), VIEW_PROPERTY(-2, "view_property"),

	GET_CITY_FEATURES(-3, "get_city_features"),

	LIST_PROPERTY_TYPE_FEATURES(-4, "list_property_type_features"),

	CREATE_PROPERTY_CREATION_REQUEST(1, "create_property_creation_request"),
	CREATE_PROPERTY_UPDATE_REQUEST(2, "create_property_update_request"),
	CREATE_PROPERTY_DELETION_REQUEST(3, "delete_property"),

	REVIEW_PROPERTY_REQUEST(4, "review_property_request"),

	CREATE_PROPERTY_LISTING(5, "create_property_listing"), UPDATE_PROPERTY_LISTING(6, "update_property_listing"),
	DELETE_PROPERTY_LISTING(7, "delete_property_listing"),

	GET_PROPERTY_LISTINGS(8, "get_property_listings"),

	UPDATE_PROPERTY_LISTING_AVAILABILITY_STATUS(9, "update_property_listing_available_status"),

	SET_CITY_FEATURES(10, "set_city_features"),

	CREATE_PROPERTY_TYPE_FEATURES(11, "create_property_type_feature"),
	DELETE_PROPERTY_TYPE_FEATURES(12, "delete_property_type_feature"),

	CREATE_PROPERTY_PRICE_RULE(13, "create_property_price_rule"),
	VIEW_PROPERTY_PRICE_RULES(14, "view_property_price_rule"),
	UPDATE_PROPERTY_PRICE_RULE(15, "update_property_price_rule"),
	DELETE_PROPERTY_PRICE_RULE(16, "delete_property_price_rule"),

	ADD_TO_OWN_SAVED_LIST(17, "add_to_own_saved_list"), 
	REMOVE_FROM_OWN_SAVED_LIST(18, "remove_from_own_saved_list"),
	GET_OWN_SAVED_LIST(19, "get_own_saved_list"),

	ADD_TO_USER_SAVED_LIST(20, "add_to_user_saved_list"),
	REMOVE_FROM_USER_SAVED_LIST(21, "remove_from_user_saved_list"), 
	GET_USER_SAVED_LIST(22, "get_user_saved_list");

	private static final String NAMESPACE = "property";

	public static class Constants {

		private static final String PREFIX = NAMESPACE + AbstractFunctionalityDelegate.NAMESPACE_DELIMETER;

		public static final String LIST_PROPERTY = PREFIX + -1;
		public static final String VIEW_PROPERTY = PREFIX + -2;
		public static final String GET_CITY_FEATURES = PREFIX + -3;
		public static final String LIST_PROPERTY_TYPE_FEATURES = PREFIX + -4;
		public static final String CREATE_PROPERTY_CREATION_REQUEST = PREFIX + 1;
		public static final String CREATE_PROPERTY_UPDATE_REQUEST = PREFIX + 2;
		public static final String CREATE_PROPERTY_DELETION_REQUEST = PREFIX + 3;
		public static final String REVIEW_PROPERTY_REQUEST = PREFIX + 4;
		public static final String CREATE_PROPERTY_LISTING = PREFIX + 5;
		public static final String UPDATE_PROPERTY_LISTING = PREFIX + 6;
		public static final String DELETE_PROPERTY_LISTING = PREFIX + 7;
		public static final String GET_PROPERTY_LISTINGS = PREFIX + 8;
		public static final String UPDATE_PROPERTY_LISTING_AVAILABILITY_STATUS = PREFIX + 9;
		public static final String SET_CITY_FEATURES = PREFIX + 10;
		public static final String CREATE_PROPERTY_TYPE_FEATURES = PREFIX + 11;
		public static final String DELETE_PROPERTY_TYPE_FEATURES = PREFIX + 12;
		public static final String CREATE_PROPERTY_PRICE_RULE = PREFIX + 13;
		public static final String VIEW_PROPERTY_PRICE_RULES = PREFIX + 14;
		public static final String UPDATE_PROPERTY_PRICE_RULE = PREFIX + 15;
		public static final String DELETE_PROPERTY_PRICE_RULE = PREFIX + 16;
		public static final String ADD_TO_OWN_SAVED_LIST = PREFIX + 17;
		public static final String REMOVE_FROM_OWN_SAVED_LIST = PREFIX + 18;
		public static final String GET_OWN_SAVED_LIST = PREFIX + 19;
		public static final String ADD_TO_USER_SAVED_LIST = PREFIX + 20;
		public static final String REMOVE_FROM_USER_SAVED_LIST = PREFIX + 21;
		public static final String GET_USER_SAVED_LIST = PREFIX + 22;
	}

	private final int id;
	private final String name;
	private final Boolean isVisible;

	private final boolean isFrontend = true;
	private final boolean isBackend = true;

	private PropertyFunctionalities(int id, String name) {
		this(id, name, true);
	}

	private PropertyFunctionalities(int id, String name, boolean isVisible) {
		this.id = id;
		this.name = name;
		this.isVisible = isVisible;
	}

	public static PropertyFunctionalities from(Integer value) {

		switch (value) {

		case -1:
			return LIST_PROPERTY;

		case -2:
			return VIEW_PROPERTY;

		case -3:
			return GET_CITY_FEATURES;

		case -4:
			return LIST_PROPERTY_TYPE_FEATURES;

		case 1:
			return CREATE_PROPERTY_CREATION_REQUEST;

		case 2:
			return CREATE_PROPERTY_UPDATE_REQUEST;

		case 3:
			return CREATE_PROPERTY_DELETION_REQUEST;

		case 4:
			return REVIEW_PROPERTY_REQUEST;

		case 5:
			return CREATE_PROPERTY_LISTING;

		case 6:
			return UPDATE_PROPERTY_LISTING;

		case 7:
			return DELETE_PROPERTY_LISTING;

		case 8:
			return GET_PROPERTY_LISTINGS;

		case 9:
			return UPDATE_PROPERTY_LISTING_AVAILABILITY_STATUS;

		case 10:
			return SET_CITY_FEATURES;

		case 11:
			return CREATE_PROPERTY_TYPE_FEATURES;

		case 12:
			return DELETE_PROPERTY_TYPE_FEATURES;

		case 13:
			return CREATE_PROPERTY_PRICE_RULE;

		case 14:
			return VIEW_PROPERTY_PRICE_RULES;

		case 15:
			return UPDATE_PROPERTY_PRICE_RULE;

		case 16:
			return DELETE_PROPERTY_PRICE_RULE;

		case 17:
			return ADD_TO_OWN_SAVED_LIST;

		case 18:
			return REMOVE_FROM_OWN_SAVED_LIST;

		case 19:
			return GET_OWN_SAVED_LIST;
	
		case 20:
			return ADD_TO_USER_SAVED_LIST;

		case 21:
			return REMOVE_FROM_USER_SAVED_LIST;

		case 22:
			return GET_USER_SAVED_LIST;

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
