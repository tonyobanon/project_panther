package com.re.paas.api.infra.database.model;

import java.util.ArrayList;
import java.util.List;

public class TableUpdate {
    /**
     * <p>
     * An array of attributes that describe the key schema for the table and indexes. If you are adding a new global
     * secondary index to the table, <code>AttributeDefinitions</code> must include the key element(s) of the new index.
     * </p>
     */
    private java.util.List<AttributeDefinition> attributeDefinitions;
    /**
     * <p>
     * The name of the table to be updated.
     * </p>
     */
    private String tableName;
    
	private List<GlobalSecondaryIndexUpdate> globalSecondaryIndexUpdates = new ArrayList<>();
	
	private Boolean ttlEnabled;
	
	private ProvisionedThroughput provisionedThroughput;

	public java.util.List<AttributeDefinition> getAttributeDefinitions() {
		return attributeDefinitions;
	}

	public TableUpdate setAttributeDefinitions(java.util.List<AttributeDefinition> attributeDefinitions) {
		this.attributeDefinitions = attributeDefinitions;
		return this;
	}

	public String getTableName() {
		return tableName;
	}

	public TableUpdate setTableName(String tableName) {
		this.tableName = tableName;
		return this;
	}

	public List<GlobalSecondaryIndexUpdate> getGlobalSecondaryIndexUpdates() {
		return globalSecondaryIndexUpdates;
	}

	public TableUpdate setGlobalSecondaryIndexUpdates(List<GlobalSecondaryIndexUpdate> globalSecondaryIndexUpdates) {
		this.globalSecondaryIndexUpdates = globalSecondaryIndexUpdates;
		return this;
	}
	
	public TableUpdate addGlobalSecondaryIndexUpdates(GlobalSecondaryIndexUpdate globalSecondaryIndexUpdate) {
		this.globalSecondaryIndexUpdates.add(globalSecondaryIndexUpdate);
		return this;
	}

	public Boolean getTtlEnabled() {
		return ttlEnabled;
	}

	public TableUpdate setTtlEnabled(Boolean ttlEnabled) {
		this.ttlEnabled = ttlEnabled;
		return this;
	}

	public ProvisionedThroughput getProvisionedThroughput() {
		return provisionedThroughput;
	}

	public TableUpdate setProvisionedThroughput(ProvisionedThroughput provisionedThroughput) {
		this.provisionedThroughput = provisionedThroughput;
		return this;
	}

}
