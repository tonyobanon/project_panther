package com.re.paas.api.infra.database.model;

import java.util.Map;

public class DeleteItemResult {
	
    private java.util.Map<String, Object> attributes;

    /**
     * Returns the content of the old item
     * 
     * @return {@link Map}
     */
	public java.util.Map<String, Object> getAttributes() {
		return attributes;
	}

	public DeleteItemResult setAttributes(java.util.Map<String, Object> attributes) {
		this.attributes = attributes;
		return this;
	}
	
}
