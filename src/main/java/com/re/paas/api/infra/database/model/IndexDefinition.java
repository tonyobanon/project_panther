package com.re.paas.api.infra.database.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 * Represents the properties of an index.
 * </p>
 */
public class IndexDefinition extends IndexDescriptor {

	private java.util.List<KeySchemaElement> keySchema;

	private Projection projection = new Projection(ProjectionType.KEYS_ONLY);

	IndexDefinition(String tableName, String indexName, Type type) {
		super(tableName, indexName, type);
	}

	public java.util.List<KeySchemaElement> getKeySchema() {
		return keySchema;
	}

	public IndexDefinition setKeySchema(Collection<KeySchemaElement> keySchema) {
		if (keySchema == null) {
			this.keySchema = null;
		} else {
			this.keySchema = new ArrayList<KeySchemaElement>(keySchema);
		}
		return this;
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

	public IndexDefinition setProjection(Projection projection) {
		this.projection = projection;
		return this;
	}

	public Projection getProjection() {
		return this.projection;
	}

	public IndexDefinition withProjection(Projection projection) {
		setProjection(projection);
		return this;
	}

}