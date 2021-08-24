package com.re.paas.api.infra.database.model.exceptions;

public class AttributeNotFoundException extends RuntimeException {

	private final String attrName;
	private final String message;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AttributeNotFoundException(String attrName) {
		this.attrName = attrName;
		this.message = "Attribute: " + attrName + " was not found";
	}
	
	public AttributeNotFoundException(String attrName, String message) {
		this.attrName = attrName;
		this.message = message;
	}
	
	public String getAttrName() {
		return attrName;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}

}
