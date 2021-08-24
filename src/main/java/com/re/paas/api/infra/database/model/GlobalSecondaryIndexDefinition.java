package com.re.paas.api.infra.database.model;

import com.re.paas.api.infra.database.textsearch.QueryType;

public class GlobalSecondaryIndexDefinition extends IndexDefinition {

	private Boolean queryOptimzed;

	private QueryType queryType;
	
	GlobalSecondaryIndexDefinition(String tableName, String indexName) {
		super(tableName, indexName, Type.GSI);
	}
	
	public GlobalSecondaryIndexDefinition(String indexName) {
		this(null, indexName);
	}
	
	public Boolean forTextSearch() {
		return queryOptimzed;
	}

	public GlobalSecondaryIndexDefinition setQueryOptimzed(Boolean queryOptimzed) {
		this.queryOptimzed = queryOptimzed;
		return this;
	}

	public QueryType getQueryType() {
		return queryType;
	}

	public GlobalSecondaryIndexDefinition setQueryType(QueryType queryType) {
		this.queryType = queryType;
		return this;
	}
}
