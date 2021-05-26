package com.re.paas.api.infra.database.model;

import java.util.ArrayList;
import java.util.Collection;

import com.re.paas.api.infra.database.textsearch.QueryType;

/**
 * <p>
 * Represents the properties of an index.
 * </p>
 */
public class IndexDefinition extends IndexDescriptor {

	private java.util.List<KeySchemaElement> keySchema;

	private Projection projection = new Projection().withProjectionType(ProjectionType.KEYS_ONLY);

	private boolean queryOptimzed;

	private QueryType queryType = QueryType.EQ;

	private ProvisionedThroughput provisionedThroughput;

	public IndexDefinition(String tableName, String indexName) {
		super(tableName, indexName);
	}

	public IndexDefinition(String indexName, Type type) {
		super(null, indexName, type);
	}

	public IndexDefinition(String tableName, String indexName, Type type) {
		super(tableName, indexName, type);
	}

	public IndexDefinition(IndexDescriptor descriptor) {
		super(descriptor.getTableName(), descriptor.getIndexName(), descriptor.getType());
	}

	public java.util.List<KeySchemaElement> getKeySchema() {
		return keySchema;
	}

	public void setKeySchema(Collection<KeySchemaElement> keySchema) {
		if (keySchema == null) {
			this.keySchema = null;
			return;
		}

		this.keySchema = new ArrayList<KeySchemaElement>(keySchema);
	}

	public IndexDefinition withKeySchema(KeySchemaElement... keySchema) {
		if (this.keySchema == null) {
			setKeySchema(new ArrayList<KeySchemaElement>(keySchema.length));
		}
		for (KeySchemaElement ele : keySchema) {
			this.keySchema.add(ele);
		}
		return this;
	}

	public IndexDefinition addHashKey(String attributeName) {
		return withKeySchema(new KeySchemaElement(attributeName, KeyType.HASH));
	}

	public IndexDefinition addRangeKey(String attributeName) {
		if (attributeName != null) {
			return withKeySchema(new KeySchemaElement(attributeName, KeyType.RANGE));
		}
		return this;
	}

	public IndexDefinition withKeySchema(Collection<KeySchemaElement> keySchema) {
		setKeySchema(keySchema);
		return this;
	}

	public void setProjection(Projection projection) {
		this.projection = projection;
	}

	public Projection getProjection() {
		return this.projection;
	}

	public IndexDefinition withProjection(Projection projection) {
		setProjection(projection);
		return this;
	}

	public boolean isQueryOptimzed() {
		return queryOptimzed;
	}

	public IndexDefinition setQueryOptimzed(boolean queryOptimzed) {
		this.queryOptimzed = queryOptimzed;
		return this;
	}

	public QueryType getQueryType() {
		return queryType;
	}

	public IndexDefinition setQueryType(QueryType queryType) {
		this.queryType = queryType;
		return this;
	}

	public ProvisionedThroughput getProvisionedThroughput() {
		return provisionedThroughput;
	}

	public IndexDefinition setProvisionedThroughput(ProvisionedThroughput provisionedThroughput) {
		this.provisionedThroughput = provisionedThroughput;
		return this;
	}

}