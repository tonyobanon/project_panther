package com.re.paas.internal.entites.directory;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Cache
@Entity
public class AgentEntity {

	@Id
	Long id;
	Long agentOrganization;
	Boolean isActive;
	Integer yearsOfExperience;
	

	public Long getId() {
		return id;
	}

	public AgentEntity setId(Long id) {
		this.id = id;
		return this;
	}
	
	public Long getAgentOrganization() {
		return agentOrganization;
	}

	public AgentEntity setAgentOrganization(Long agentOrganization) {
		this.agentOrganization = agentOrganization;
		return this;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public AgentEntity setIsActive(Boolean isActive) {
		this.isActive = isActive;
		return this;
	}
	
	public Integer getYearsOfExperience() {
		return yearsOfExperience;
	}

	public AgentEntity setYearsOfExperience(Integer yearsOfExperience) {
		this.yearsOfExperience = yearsOfExperience;
		return this;
	}
	
}
