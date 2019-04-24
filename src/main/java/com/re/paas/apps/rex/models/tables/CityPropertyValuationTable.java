package com.re.paas.apps.rex.models.tables;

import java.util.Date;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.CityPropertyValuationTableSpec;

public class CityPropertyValuationTable implements BaseTable {

	String id;

	Integer averagePriceMin;
	Integer averagePriceMax;
	
	Date dateCreated;
	
	@Override
	public String hashKey() {
		return CityPropertyValuationTableSpec.ID;
	}
	
}
