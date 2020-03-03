package com.re.paas.integrated.tables.defs.base;

import java.util.Date;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.integrated.tables.spec.base.ConfigTableSpec;

public class ConfigTable implements BaseTable {

	String key;
	String value;
	Integer intValue;
	
	Date dateCreated;
	Date dateUpdated;

	@Override
	public String hashKey() {
		return ConfigTableSpec.KEY;
	}

}
