package com.re.paas.apps.rex.models.tables;

import java.util.Date;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.PropertyPOITableSpec;

public class PropertyPOITable implements BaseTable {

	Long id;

	String school;
	String restaurant;
	String bank;
	String publicTransportation;
	
	Date dateCreated;

	@Override
	public String hashKey() {
		return PropertyPOITableSpec.ID;
	}
	
}
