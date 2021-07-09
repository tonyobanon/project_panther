package com.re.paas.api.infra.database.model;

public class LocalSecondaryIndexDefinition extends IndexDefinition {

	public LocalSecondaryIndexDefinition(String indexName) {
		super(null, indexName, Type.LSI);
	}
}
