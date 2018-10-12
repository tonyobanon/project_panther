package com.re.paas.api.forms;

public class FieldDescriptor {

	private final FieldType type;
	private final boolean isDefault;
	
	public FieldDescriptor(FieldType type, boolean isDefault) {
		this.type = type;
		this.isDefault = isDefault;
	}

	public FieldType getType() {
		return type;
	}

	public boolean isDefault() {
		return isDefault;
	}
	
}
