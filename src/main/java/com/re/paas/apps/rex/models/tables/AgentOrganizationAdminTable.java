package com.re.paas.apps.rex.models.tables;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.AgentOrganizationAdminTableSpec;

public class AgentOrganizationAdminTable implements BaseTable {
	
	Long id;
	
	Long agentOrganization;

	@Override
	public String hashKey() {
		return AgentOrganizationAdminTableSpec.ID;
	}
	
}
