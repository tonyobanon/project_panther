package com.re.paas.integrated.tables.defs.payments;

import java.util.Date;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.integrated.tables.spec.payments.CurrencyRatesTableSpec;

public class CurrencyRatesTable implements BaseTable {

	String pair;
	Double rate;
	Date lastUpdated;

	@Override
	public String hashKey() {
		return CurrencyRatesTableSpec.PAIR;
	}
}
