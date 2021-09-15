package com.re.paas.api.infra.database.textsearch;

import java.util.function.Consumer;

import com.re.paas.api.infra.database.document.Item;

public interface TextSearch {

	public QueryInterface getQueryInterface();
	
	boolean search(String checkpointId, Consumer<Item> consumer);

	void add(SearchGraphId id, Integer entityType, String[] matrix);

	void remove(SearchGraphId id);
}
