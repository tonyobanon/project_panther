package com.re.paas.internal.entites.directory;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Cache
@Entity
public class AgentOrganizationAdminEntity {
	
	@Id
	Long id;
	
	Long agentOrganization;

	public Long getId() {
		return id;
	}

	public AgentOrganizationAdminEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getAgentOrganization() {
		return agentOrganization;
	}

	public AgentOrganizationAdminEntity setAgentOrganization(Long agentOrganization) {
		this.agentOrganization = agentOrganization;
		return this;
	}
	
}
