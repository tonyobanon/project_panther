package com.re.paas.internal.tables.defs.locations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.tables.spec.locations.PublicHolidayTableSpec;

public class PublicHolidayTable implements BaseTable {
 
	Long id;
	String name;
	String country;
	boolean isPublic;
	Date date;
	Date dateCreated;

	@Override
	public String hashKey() {
		return PublicHolidayTableSpec.ID;
	}

	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition countryIndex = new IndexDefinition(PublicHolidayTableSpec.COUNTRY_INDEX, Type.GSI)
		.addHashKey(PublicHolidayTableSpec.COUNTRY);
		
		indexes.add(countryIndex);
		
		return indexes;
	}
}
