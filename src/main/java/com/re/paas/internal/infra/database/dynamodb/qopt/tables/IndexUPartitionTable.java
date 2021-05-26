package com.re.paas.internal.infra.database.dynamodb.qopt.tables;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.model.Projection;
import com.re.paas.api.infra.database.model.ProjectionType;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.infra.database.dynamodb.qopt.attributes.IndexUPartitionSpec;

public class IndexUPartitionTable implements BaseTable {

	public String id;
	public Integer partitionId;
	public Integer size;

	@Override
	public String hashKey() {
		return IndexUPartitionSpec.ID;
	}
	
	@Override
	public String rangeKey() {
		return IndexUPartitionSpec.PARTITION_ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition sizeIndex = new IndexDefinition(IndexUPartitionSpec.SIZE_INDEX, Type.LSI)
		.addHashKey(IndexUPartitionSpec.ID)
		.addRangeKey(IndexUPartitionSpec.SIZE)
		.withProjection(new Projection().withProjectionType(ProjectionType.ALL));
		
		indexes.add(sizeIndex);
		
		return indexes;
	}

}
