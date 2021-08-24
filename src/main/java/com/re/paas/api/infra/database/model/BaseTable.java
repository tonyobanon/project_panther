package com.re.paas.api.infra.database.model;

import java.util.Collections;
import java.util.List;

import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Table;

public interface BaseTable {
	
	default Boolean enabled() {
		return true;
	}
	
	default String name() {
		return this.getClass().getSimpleName();
	}
	
	String hashKey();
	
	default String rangeKey(){
		return null;
	}
	
	default List<GlobalSecondaryIndexDefinition> globalSecondaryIndexes() {
		return Collections.emptyList();
	}
	
	default List<LocalSecondaryIndexDefinition> localSecondaryIndexes() {
		return Collections.emptyList();
	}
	
	default Table get() {
		return Database.get().getTable(name());
	}
}
