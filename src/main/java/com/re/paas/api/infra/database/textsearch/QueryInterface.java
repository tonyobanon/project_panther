package com.re.paas.api.infra.database.textsearch;

import java.util.Map;
import java.util.function.Consumer;

import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.xspec.Condition;
import com.re.paas.api.infra.database.model.IndexDescriptor;

public interface QueryInterface {

	Map<String, Checkpoint> getCheckpoints();

	Map<String, Integer> getOpenQueries();

	String newSearchKey(String keyword, int limit, Integer... entityTypes);

	void parallelQuery(IndexDescriptor index, Condition rangeCondition, Consumer<Item> consumer, String... projections);

}