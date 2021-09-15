package com.re.paas.internal.infra.database.dynamodb.tables.definitions;

import com.re.paas.api.infra.database.model.BaseTable;
import com.re.paas.internal.infra.database.dynamodb.tables.attributes.IndexPartitionSpec;

public class IndexPartitionTable implements BaseTable {

	public String id;
	public Integer partition;
	public Integer size;

	@Override
	public String name() {
		return IndexPartitionSpec.TABLE_NAME;
	}
	
	@Override
	public String hashKey() {
		return IndexPartitionSpec.ID;
	}

	@Override
	public String rangeKey() {
		return IndexPartitionSpec.PARTITION;
	}
}
