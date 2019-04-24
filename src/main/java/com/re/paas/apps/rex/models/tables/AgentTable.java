package com.re.paas.apps.rex.models.tables;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.AgentTableSpec;

public class AgentTable implements BaseTable {

	Long id;
	Long agentOrganization;
	Boolean isActive;
	Integer yearsOfExperience;
	
	@Override
	public String hashKey() {
		return AgentTableSpec.ID;
	}
}
