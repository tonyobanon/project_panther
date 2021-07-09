package com.re.paas.api.infra.database.model;

public class BaseTableDefinition {

	private java.util.List<AttributeDefinition> attributeDefinitions;
	
	private String tableName;

	private java.util.List<KeySchemaElement> keySchema;

	public java.util.List<AttributeDefinition> getAttributeDefinitions() {
		return attributeDefinitions;
	}

	public void setAttributeDefinitions(java.util.Collection<AttributeDefinition> attributeDefinitions) {
		if (attributeDefinitions == null) {
			this.attributeDefinitions = null;
			return;
		}

		this.attributeDefinitions = new java.util.ArrayList<AttributeDefinition>(attributeDefinitions);
	}

	public void setAttributeDefinitions(AttributeDefinition... attributeDefinitions) {
		if (this.attributeDefinitions == null) {
			setAttributeDefinitions(new java.util.ArrayList<AttributeDefinition>(attributeDefinitions.length));
		}
		for (AttributeDefinition ele : attributeDefinitions) {
			this.attributeDefinitions.add(ele);
		}
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return this.tableName;
	}

	public java.util.List<KeySchemaElement> getKeySchema() {
		return keySchema;
	}

	public void setKeySchema(java.util.Collection<KeySchemaElement> keySchema) {
		if (keySchema == null) {
			this.keySchema = null;
			return;
		}

		this.keySchema = new java.util.ArrayList<KeySchemaElement>(keySchema);
	}
}
