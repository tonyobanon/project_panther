package com.re.paas.internal.errors;

import com.re.paas.api.errors.Error;

public enum RealmError implements Error {

	REALM_NAME_MISMATCH(5,
			"The realm definition: {ref1} could not be added using name {ref2}. It may be overriding name() from its superclass"),

	REALM_NAME_ALREADY_EXISTS(10, "The realm name: {ref1} defined in {ref2} already exists"),

	DUPLICATE_FUNCTIONALITY(15, "Functionality: {ref1} defined by {ref2} is already in use by the specified realm"),

	DUPLICATE_FORM_QUESTION(20, "Question {ref1} defined in realm definition {ref2} already exists"),

	DUPLICATE_FORM_SECTION(25, "Section {ref1} defined in realm definition {ref2} already exists"),

	INVALID_FORM_SECTION_IDENTIFIER(30, "Invalid identifier for section defined in realm definition {ref1}"),

	INVALID_FORM_QUESTION_IDENTIFIER(35,
			"Invalid identifier for question in section: {ref1} defined in realm definition {ref2}"),

	// Add here

	// Add here
	
	DUPLICATE_FORM_ELEMENT_IDENTIFIER(50,
			"Duplicate identifier: {ref1} used for form defined in realm definition {ref2}"),

	INVALIDATE_FORM_FIELD(55, "An invalid form field was detected at reference: {ref1} in realm definition {ref2}"),

	INVALID_SECTION_REFERENCE(60, "An invalid section reference: {ref1} was declared in realm definition {ref2}"),

	BASE_REALM_IN_USE_FUNCTIONALITIES_ADDED(65,
			"Realm {ref1} is currently in use. More functionalities have been added by another realm definition"),
	
	BASE_REALM_IN_USE_FORM_SECTION_ADDED(70,
			"Realm {ref1} is currently in use. The onboarding form has been modified by another realm definition"),

	REALM_IN_USE_BY_OTHER_REALMS(75, "Realm {ref1} is currently in use by other realm(s)"),

	REALM_IN_USE_BY_ROLES(80, "Functionality: {ref1} specified in realm definition {ref2} is currently in use");

	
	private boolean isFatal;
	private int code;
	private String message;

	private RealmError(Integer code, String message) {
		this(code, message, false);
	}

	private RealmError(Integer code, String message, boolean isFatal) {
		this.code = code;
		this.message = message;
		this.isFatal = isFatal;
	}

	@Override
	public String namespace() {
		return "realm";
	}

	public static RealmError from(int value) {

		switch (value) {

		case 5:
			return REALM_NAME_MISMATCH;
		case 10:
			return REALM_NAME_ALREADY_EXISTS;
		case 15:
			return DUPLICATE_FUNCTIONALITY;
		case 20:
			return DUPLICATE_FORM_QUESTION;
		case 25:
			return DUPLICATE_FORM_SECTION;
		case 30:
			return INVALID_FORM_SECTION_IDENTIFIER;
		case 35:
			return INVALID_FORM_QUESTION_IDENTIFIER;
		case 50:
			return DUPLICATE_FORM_ELEMENT_IDENTIFIER;
		case 55:
			return INVALIDATE_FORM_FIELD;
		case 60:
			return INVALID_SECTION_REFERENCE;
		case 65:
			return BASE_REALM_IN_USE_FUNCTIONALITIES_ADDED;
		case 70:
			return BASE_REALM_IN_USE_FORM_SECTION_ADDED;
		case 75:
			return REALM_IN_USE_BY_OTHER_REALMS;
		case 80:
			return REALM_IN_USE_BY_ROLES;
		default:
			return null;
		}
	}

	@Override
	public boolean isFatal() {
		return isFatal;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
