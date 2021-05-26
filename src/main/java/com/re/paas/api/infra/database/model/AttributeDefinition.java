package com.re.paas.api.infra.database.model;

/**
 * <p>
 * Represents an attribute for describing the key schema for the table and indexes.
 * </p>
 */
public class AttributeDefinition {

 
    private String attributeName;

    private ScalarAttributeType attributeType;

    public AttributeDefinition() {
    }

    public AttributeDefinition(String attributeName, String attributeType) {
    	this(attributeName, ScalarAttributeType.fromValue(attributeType));
    }

    public AttributeDefinition(String attributeName, ScalarAttributeType attributeType) {
        setAttributeName(attributeName);
        setAttributeType(attributeType);
    }

    public AttributeDefinition setAttributeName(String attributeName) {
        this.attributeName = attributeName;
        return this;
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public ScalarAttributeType getAttributeType() {
        return this.attributeType;
    }

    public AttributeDefinition setAttributeType(ScalarAttributeType attributeType) {
    	this.attributeType = attributeType;
        return this;
    }
 
}