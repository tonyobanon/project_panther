package com.re.paas.internal.models.tables.locations;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.models.tables.spec.TerritoryTableSpec;

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
		
		IndexDefinition countryCodeIndex = new IndexDefinition(TerritoryTableSpec.COUNTRY_CODE_INDEX, Type.GSI)
		.addHashKey(TerritoryTableSpec.COUNTRY_CODE);

		indexes.add(countryCodeIndex);
		
		return indexes;
	}

}
