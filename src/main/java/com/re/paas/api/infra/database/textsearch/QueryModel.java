package com.re.paas.api.infra.database.textsearch;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.xspec.Condition;
import com.re.paas.api.infra.database.model.IndexDescriptor;

public interface QueryModel {

	void loadIndexedGSIs();

	Map<String, TextSearchCheckpoint> getQueryCheckpoints();

	Map<String, Integer> getOpenQueries();

	String newSearchKey(String keyword, int limit, Integer... entityTypes);

	void parallelQuery(IndexDescriptor index, Condition rangeCondition, Consumer<Item> consumer, String... projections);
	
	void addIndex(IndexDescriptor index, List<String> projections, QueryType queryType, String indexHashKey,
			String indexRangeKey, Long readThroughputCapacity, String tableHashKey, String tableRangeKey);

	Integer partitionCount(IndexDescriptor index);

	Integer nextPartitionKey(IndexDescriptor index, int size);

	void incrementPartition(IndexDescriptor index, int partitionId, int size) throws PartitionSizeExceeded;

	void decrementPartition(IndexDescriptor index, int partitionId, int size);

}