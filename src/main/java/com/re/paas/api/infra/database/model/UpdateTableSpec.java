package com.re.paas.api.infra.database.model;

public class UpdateTableSpec {
    /**
     * <p>
     * An array of attributes that describe the key schema for the table and indexes. If you are adding a new global
     * secondary index to the table, <code>AttributeDefinitions</code> must include the key element(s) of the new index.
     * </p>
     */
    private java.util.List<AttributeDefinition> attributeDefinitions;

	public java.util.List<AttributeDefinition> getAttributeDefinitions() {
		return attributeDefinitions;
	}

	public UpdateTableSpec setAttributeDefinitions(java.util.List<AttributeDefinition> attributeDefinitions) {
		this.attributeDefinitions = attributeDefinitions;
		return this;
	}
}
