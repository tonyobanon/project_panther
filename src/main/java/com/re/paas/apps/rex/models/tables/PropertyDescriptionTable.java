package com.re.paas.apps.rex.models.tables;

import java.util.Date;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.PropertyDescriptionTableSpec;

public class PropertyDescriptionTable implements BaseTable {
	
	Long id;
	
	String description;
	Date dateCreated;

	@Override
	public String hashKey() {
		return PropertyDescriptionTableSpec.ID;
	}
}
