package com.re.paas.internal.infra.database.tables.definitions;

import java.util.List;

import com.re.paas.api.infra.database.model.GlobalSecondaryIndexDefinition;
import com.re.paas.api.infra.database.model.Projection;
import com.re.paas.api.infra.database.model.ProjectionType;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.infra.database.tables.attributes.MatrixSpec;

public class MatrixTable implements BaseTable {

	public String id;
	public Integer matrixId;

	public Integer entityType;

	public Integer matrixHashKey;
	public String matrix;
	
	@Override
	public String name() {
		return MatrixSpec.TABLE_NAME;
	}
	
	@Override
	public String hashKey() {
		return MatrixSpec.ID;
	}
	
	@Override
	public String rangeKey() {
		return MatrixSpec.MATRIX_ID;
	}
	
	@Override
	public List<GlobalSecondaryIndexDefinition> globalSecondaryIndexes() {
		return List.of(
				(GlobalSecondaryIndexDefinition) 
				new GlobalSecondaryIndexDefinition(MatrixSpec.MATRIX_INDEX)
				.setQueryOptimzed(true)
				.addHashKey(MatrixSpec.MATRIX_HASHKEY)
				.addRangeKey(MatrixSpec.MATRIX)
				.withProjection(new Projection(ProjectionType.INCLUDE)
						.withNonKeyAttributes(MatrixSpec.ENTITY_TYPE))
			);
	}

}
