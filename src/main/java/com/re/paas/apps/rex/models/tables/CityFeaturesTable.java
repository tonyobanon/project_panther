package com.re.paas.apps.rex.models.tables;

import java.util.Date;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.CityFeaturesTableSpec;

public class CityFeaturesTable implements BaseTable {
 
	String id;

	Integer water;
	Integer goodRoad;
	Integer security;
	Integer socialization;
	Integer power;

	Date dateCreated;
	
	@Override
	public String hashKey() {
		return CityFeaturesTableSpec.ID;
	}
	
}
