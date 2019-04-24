package com.re.paas.internal.models.tables.locations;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.models.tables.spec.LanguageTableSpec;

public class LanguageTable implements BaseTable {

	String code;
	String langName;

	@Override
	public String hashKey() {
		return LanguageTableSpec.CODE;
	}
}
