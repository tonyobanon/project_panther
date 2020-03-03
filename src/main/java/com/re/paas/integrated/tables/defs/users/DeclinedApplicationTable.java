package com.re.paas.integrated.tables.defs.users;

import java.util.Date;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.integrated.tables.spec.users.DeclinedApplicationTableSpec;

public class DeclinedApplicationTable implements BaseTable {

	Long applicationId;

	Long staffId;
	Integer reason;

	Date dateCreated;

	@Override
	public String hashKey() {
		return DeclinedApplicationTableSpec.APPLICATION_ID;
	}
	
}
