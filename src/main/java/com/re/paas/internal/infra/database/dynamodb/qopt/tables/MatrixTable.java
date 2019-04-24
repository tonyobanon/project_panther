package com.re.paas.internal.infra.database.dynamodb.qopt.tables;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.model.Projection;
import com.re.paas.api.infra.database.model.ProjectionType;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.infra.database.dynamodb.qopt.attributes.MatrixSpec;

public class MatrixTable implements BaseTable {

	public String id;
	public Integer matrixId;

	public Integer entityType;

	public Integer matrixHashKey;
	public String matrix;
	
	@Override
	public String hashKey() {
		return MatrixSpec.ID;
	}
	
	@Override
	public String rangeKey() {
		return MatrixSpec.MATRIX_ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition sizeIndex = new IndexDefinition(MatrixSpec.MATRIX_INDEX, Type.GSI)
		.setQueryOptimzed(true)
		.addHashKey(MatrixSpec.MATRIX_HASHKEY)
		.addRangehKey(MatrixSpec.MATRIX)
		.withProjection(new Projection().withProjectionType(ProjectionType.INCLUDE).withNonKeyAttributes(MatrixSpec.ENTITY_TYPE));
		
		indexes.add(sizeIndex);
		
		return indexes;
	}

}
