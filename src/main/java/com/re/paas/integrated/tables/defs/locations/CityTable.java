package com.re.paas.integrated.tables.defs.locations;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.integrated.tables.spec.locations.CityTableSpec;

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
		
		IndexDefinition territoryIndex = new IndexDefinition(CityTableSpec.TERRITOTY_INDEX, Type.GSI)
		.addHashKey(CityTableSpec.TERRITOTY_CODE);

		indexes.add(territoryIndex);
		
		return indexes;
	}

}
