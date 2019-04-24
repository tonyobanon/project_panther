package com.re.paas.internal.models.tables;

import java.util.Date;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.models.tables.spec.ConfigTableSpec;

public class ConfigTable implements BaseTable {

	String key;
	Object value;
	Date dateUpdated;

	@Override
	public String hashKey() {
		return ConfigTableSpec.KEY;
	}

}
