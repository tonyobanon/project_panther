package com.re.paas.apps.rex.models.tables;

import java.util.Date;
import java.util.List;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.PropertyFloorPlanTableSpec;

public class PropertyFloorPlanTable implements BaseTable {

	Long id;

	Integer floor;
	String description;

	Integer roomCount;
	Integer bathroomCount;
	Integer area;

	List<String> images;
	Date dateUpdated;

	@Override
	public String hashKey() {
		return PropertyFloorPlanTableSpec.ID;
	}
}
