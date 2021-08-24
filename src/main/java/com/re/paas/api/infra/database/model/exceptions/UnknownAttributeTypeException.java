package com.re.paas.api.infra.database.model.exceptions;

public class UnknownAttributeTypeException extends RuntimeException {

	private final String attrName;
	private final String attrType;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UnknownAttributeTypeException(String attrName, String attrType) {
		this.attrName = attrName;
		this.attrType = attrType;
	}
	
	public String getAttrName() {
		return attrName;
	}
	
	public String getAttrType() {
		return attrType;
	}
	
	@Override
	public String getMessage() {
		return "Unknnown type: " +  attrType + " for atrtribute: " + attrName;
	}

}
