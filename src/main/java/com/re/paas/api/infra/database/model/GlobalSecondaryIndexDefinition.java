package com.re.paas.api.infra.database.model;

import com.re.paas.api.infra.database.textsearch.QueryType;

public class GlobalSecondaryIndexDefinition extends IndexDefinition {

	private boolean queryOptimzed;

	private QueryType queryType;
	
	GlobalSecondaryIndexDefinition(String tableName, String indexName) {
		super(tableName, indexName, Type.GSI);
	}
	
	public GlobalSecondaryIndexDefinition(String indexName) {
		this(null, indexName);
	}
	
	public boolean forTextSearch() {
		return queryOptimzed;
	}

	public GlobalSecondaryIndexDefinition setQueryOptimzed(boolean queryOptimzed) {
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
