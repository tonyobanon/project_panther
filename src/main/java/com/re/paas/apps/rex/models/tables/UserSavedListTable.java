package com.re.paas.apps.rex.models.tables;

import java.util.Date;
import java.util.List;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.UserSavedListTableSpec;

public class UserSavedListTable implements BaseTable {

	Long id;
	List<Long> properties;
	Date dateUpdated;
	
	@Override
	public String hashKey() {
		return UserSavedListTableSpec.ID;
	}
}
