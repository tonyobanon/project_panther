package com.re.paas.internal.models.tables;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.models.tables.spec.IndexedNameTableSpec;

public class IndexedNameTable implements BaseTable {

	Long id;
	String entityId;
	String type;
	String x;
	String y;
	String z;

	@Override
	public String hashKey() {
		return IndexedNameTableSpec.ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition entityIdIndex = new IndexDefinition(IndexedNameTableSpec.ENTITY_ID_INDEX, Type.GSI)
		.addHashKey(IndexedNameTableSpec.ENTITY_ID);

		IndexDefinition xIndex = new IndexDefinition(IndexedNameTableSpec.X_INDEX, Type.GSI)
				.addHashKey(IndexedNameTableSpec.X);
		
		IndexDefinition yIndex = new IndexDefinition(IndexedNameTableSpec.Y_INDEX, Type.GSI)
				.addHashKey(IndexedNameTableSpec.Y);
		
		IndexDefinition zIndex = new IndexDefinition(IndexedNameTableSpec.Z_INDEX, Type.GSI)
				.addHashKey(IndexedNameTableSpec.Z);
		
		indexes.add(entityIdIndex);
		indexes.add(xIndex);
		indexes.add(yIndex);
		indexes.add(zIndex);
	
		return indexes;
	}

}
