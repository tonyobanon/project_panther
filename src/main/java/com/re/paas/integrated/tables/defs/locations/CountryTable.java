package com.re.paas.integrated.tables.defs.locations;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.integrated.tables.spec.locations.CountryTableSpec;

public class CountryTable implements BaseTable {

	String code;
	String countryName;

	String currencyCode;
	String currencyName;

	List<String> spokenLanguages;

	String languageCode;
	String dialingCode;

	@Override
	public String hashKey() {
		return CountryTableSpec.CODE;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition currencyIndex = new IndexDefinition(CountryTableSpec.CURRENCY_INDEX, Type.GSI)
		.addHashKey(CountryTableSpec.CURRENCY_CODE)
		.addRangehKey(CountryTableSpec.CURRENCY_NAME);
		
		indexes.add(currencyIndex);
		
		return indexes;
	}
	
}
