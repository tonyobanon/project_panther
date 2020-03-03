package com.re.paas.integrated.tables.defs.locations;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.integrated.tables.spec.locations.LanguageTableSpec;

public class LanguageTable implements BaseTable {

	String code;
	String langName;

	@Override
	public String hashKey() {
		return LanguageTableSpec.CODE;
	}
}
