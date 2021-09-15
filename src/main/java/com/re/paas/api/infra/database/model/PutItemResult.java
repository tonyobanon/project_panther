package com.re.paas.api.infra.database.model;

import java.util.Map;

public class PutItemResult {

	private java.util.Map<String, Object> attributes;

	/**
	 * If PutItem overwrote an attribute name-value pair, then the content of the
	 * old item is returned.
	 * 
	 * @return {@link Map}
	 */
	public java.util.Map<String, Object> getAttributes() {
		return attributes;
	}

	public PutItemResult setAttributes(java.util.Map<String, Object> attributes) {
		this.attributes = attributes;
		return this;
	}

}
