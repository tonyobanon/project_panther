package com.re.paas.api.forms;

public abstract class BaseSimpleField extends AbstractField {

	private String title;
	private Object helperText;
	
	private Integer sortOrder;
	private Object context;

	private Boolean isRequired;
	private Boolean isVisible;
	private Boolean isDefault;

	public BaseSimpleField(Object id, String title) {
		super(id != null ? id.toString() : null);
		this.title = title;
		this.sortOrder = 0;
		
		this.isRequired = true;
		this.isVisible = true;
		this.isDefault = false;
	}

	public String getTitle() {
		return title;
	}

	public BaseSimpleField setTitle(String title) {
		this.title = title;
		return this;
	}

	public BaseSimpleField setId(String id) {
		super.id = id;
		return this;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public BaseSimpleField setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
		return this;
	}

	public Object getContext() {
		return context;
	}

	public BaseSimpleField setContext(Object context) {
		this.context = context;
		return this;
	}

	public Boolean getIsRequired() {
		return isRequired;
	}

	public BaseSimpleField setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
		return this;
	}

	public Boolean getIsVisible() {
		return isVisible;
	}

	public BaseSimpleField setIsVisible(Boolean isVisible) {
		this.isVisible = isVisible;
		return this;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public BaseSimpleField setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
		return this;
	}

	public Object getHelperText() {
		return helperText;
	}

	public BaseSimpleField setHelperText(Object helperText) {
		this.helperText = helperText;
		return this;
	}
}
