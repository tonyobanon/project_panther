package com.re.paas.api.forms;

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
	public void importData(AbstractField source) {

		SimpleField sourceField = (SimpleField) source;

		this.setId(sourceField.getId());

		this.setTitle(sourceField.getTitle());
		this.setSortOrder(sourceField.getSortOrder());
		this.setContext(sourceField.getContext());

		this.setIsRequired(sourceField.getIsRequired());
		this.setIsVisible(sourceField.getIsVisible());
		this.setIsDefault(sourceField.getIsDefault());

		this.withInputType(sourceField.getInputType());
		this.setDefaultValue(sourceField.getDefaultValue());
		this.withTextValue(sourceField.getTextValue());
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
