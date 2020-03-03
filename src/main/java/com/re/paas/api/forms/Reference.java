package com.re.paas.api.forms;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ModifyType;

public class Reference {

	private ModifyType modifyType = ModifyType.ADD;
	private final String value;

	Reference(String value) {

		if (value.contains("/")) {
			Exceptions.throwRuntime("Invalid reference: " + value);
		}

		this.value = value;
	}

	public final String value() {
		return this.value;
	}

	public static final Reference create(String value) {
		return new Reference(value);
	}

	public final boolean equals(Reference obj) {
		return obj.value().equals(value());
	}

	public final ModifyType getModifyType() {
		return modifyType;
	}

	public final Reference setModifyType(ModifyType modifyType) {
		this.modifyType = modifyType;
		return this;
	}

	public String asString() {
		return value() != null ? value() : "";
	}
	
	public static enum RefType {
		SECTION, FIELD
	}
}
