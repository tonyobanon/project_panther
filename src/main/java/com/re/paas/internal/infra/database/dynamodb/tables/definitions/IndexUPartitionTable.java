package com.re.paas.internal.infra.database.dynamodb.tables.definitions;

import java.util.List;

import com.re.paas.api.infra.database.model.BaseTable;
import com.re.paas.api.infra.database.model.LocalSecondaryIndexDefinition;
import com.re.paas.api.infra.database.model.Projection;
import com.re.paas.api.infra.database.model.ProjectionType;
import com.re.paas.internal.infra.database.dynamodb.tables.attributes.IndexUPartitionSpec;

public class IndexUPartitionTable implements BaseTable {

	public String id;
	public Integer partitionId;
	public Integer size;

	@Override
	public String name() {
		return IndexUPartitionSpec.TABLE_NAME;
	}
	
	@Override
	public String hashKey() {
		return IndexUPartitionSpec.ID;
	}

	@Override
	public String rangeKey() {
		return IndexUPartitionSpec.PARTITION_ID;
	}

	@Override
	public List<LocalSecondaryIndexDefinition> localSecondaryIndexes() {
		return List.of(
				(LocalSecondaryIndexDefinition) 
				new LocalSecondaryIndexDefinition(IndexUPartitionSpec.SIZE_INDEX)
				.addHashKey(IndexUPartitionSpec.ID).addRangeKey(IndexUPartitionSpec.SIZE)
				.withProjection(new Projection(ProjectionType.ALL))
			);
	}

}
