package com.re.paas.api.infra.database.model;

import java.util.Map;

public class UpdateItemResult {
	
    private java.util.Map<String, Object> attributes;

    /**
     * Returns only the updated attributes, as they appear after the UpdateItem operation.
     * 
     * @return {@link Map}
     */
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public UpdateItemResult setAttributes(java.util.Map<String, Object> attributes) {
		this.attributes = attributes;
		return this;
	}
	
}
