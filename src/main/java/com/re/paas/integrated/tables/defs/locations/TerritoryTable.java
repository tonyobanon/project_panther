package com.re.paas.integrated.tables.defs.locations;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.integrated.tables.spec.locations.TerritoryTableSpec;

public class TerritoryTable implements BaseTable{

	String code;
	String countryCode;

	String territoryName;

	@Override
	public String hashKey() {
		return TerritoryTableSpec.CODE;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition countryCodeIndex = new IndexDefinition(TerritoryTableSpec.COUNTRY_INDEX, Type.GSI)
		.addHashKey(TerritoryTableSpec.COUNTRY_CODE)
		.addRangehKey(TerritoryTableSpec.TERRITORY_NAME);

		indexes.add(countryCodeIndex);
		
		return indexes;
	}

}
