package com.re.paas.internal.infra.database.textsearch;

import java.util.Collection;
import java.util.List;

import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.KeyAttribute;
import com.re.paas.api.infra.database.model.IndexDescriptor;
import com.re.paas.api.infra.database.textsearch.QueryType;

public interface AttributeModel {

	void addIndex(IndexDescriptor index, List<String> projections, String hashAttribute, String rangeAttribute,
			Long readThroughputCapacity, QueryType queryType, String tableHashKey, String tableRangeKey);

	Integer getIndexSize(IndexDescriptor index, String indexRangeKey, Object indexRangeKeyValue, Item item);

	void putValue(String tableName, Item item);

	void updateValue(String tableName, Item item, String hashKey, Object hashValue, String rangeKey, Object rangeValue);

	void deleteValue(String tableName, KeyAttribute... keys);

	void deleteValue(String tableName, Collection<KeyAttribute> keys);

	void deleteValue(String tableName, Object tableHashValue);

	void deleteValue(String tableName, Object tableHashValue, String indexName);

}