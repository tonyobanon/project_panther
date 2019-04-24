package com.re.paas.internal.models.tables.locations;

import java.util.List;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.models.tables.spec.CountryTableSpec;

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
	
}
