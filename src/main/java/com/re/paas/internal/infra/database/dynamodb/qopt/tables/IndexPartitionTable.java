package com.re.paas.internal.infra.database.dynamodb.qopt.tables;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.infra.database.dynamodb.qopt.attributes.IndexPartitionSpec;

public class IndexPartitionTable implements BaseTable {

	public String id;
	public Integer partition;
	public Integer size;

	@Override
	public String hashKey() {
		return IndexPartitionSpec.ID;
	}

	@Override
	public String rangeKey() {
		return IndexPartitionSpec.PARTITION;
	}
}
