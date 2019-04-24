package com.re.paas.internal.models.tables.locations;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.models.tables.spec.CityTableSpec;

public class CityTable implements BaseTable {

	String id;
	String territoryCode;

	String name;

	Double latitude;
	Double longitude;

	String timezone;

	@Override
	public String hashKey() {
		return CityTableSpec.ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition territoryCodeIndex = new IndexDefinition(CityTableSpec.TERRITOTY_CODE_INDEX, Type.GSI)
		.addHashKey(CityTableSpec.TERRITOTY_CODE);

		indexes.add(territoryCodeIndex);
		
		return indexes;
	}

}
