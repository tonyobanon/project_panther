package com.re.paas.api.forms;

import com.re.paas.api.forms.input.InputType;

public class SimpleField extends BaseSimpleField {

	private InputType inputType;
	private String defaultValue;

	private String textValue;

	SimpleField() {
		super(null, null);
	}

	public SimpleField(InputType inputType, Object title) {
		this(null, inputType, title);
	}

	public SimpleField(Object id, InputType inputType, Object title) {
		super(id, title);
		this.inputType = inputType;
	}

	@Override
	public SimpleField importData(AbstractField source, boolean copyReference) {

		SimpleField sourceField = (SimpleField) source;

		if (copyReference && sourceField.getReference() != null) {
			this.setReference(sourceField.getReference());
		}
		
		if (sourceField.getId() != null) {
			this.setId(sourceField.getId());
		}

		if (sourceField.getTitle() != null) {
			this.setTitle(sourceField.getTitle());
		}

		if (sourceField.getSortOrder() != null) {
			this.setSortOrder(sourceField.getSortOrder());
		}

		if (sourceField.getContext() != null) {
			this.setContext(sourceField.getContext());
		}

		if (sourceField.getIsRequired() != null) {
			this.setIsRequired(sourceField.getIsRequired());
		}

		if (sourceField.getIsVisible() != null) {
			this.setIsVisible(sourceField.getIsVisible());
		}

		if (sourceField.getIsDefault() != null) {
			this.setIsDefault(sourceField.getIsDefault());
		}

		if (sourceField.getInputType() != null) {
			this.withInputType(sourceField.getInputType());
		}

		if (sourceField.getDefaultValue() != null) {
			this.setDefaultValue(sourceField.getDefaultValue());
		}

		if (sourceField.getTextValue() != null) {
			this.withTextValue(sourceField.getTextValue());
		}
		
		return this;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public SimpleField setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	public InputType getInputType() {
		return inputType;
	}

	public SimpleField withInputType(InputType inputType) {
		this.inputType = inputType;
		return this;
	}

	public String getTextValue() {
		return textValue;
	}

	public SimpleField withTextValue(String textValue) {
		this.textValue = textValue;
		return this;
	}
}
