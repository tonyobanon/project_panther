package com.re.paas.api.infra.database.modelling;

import java.util.Collections;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;

public interface BaseTable {
	
	default Boolean enabled() {
		return true;
	}
	
	String hashKey();
	
	default String rangeKey(){
		return null;
	}
	
	default List<IndexDefinition> indexes() {
		return Collections.emptyList();
	}
}
