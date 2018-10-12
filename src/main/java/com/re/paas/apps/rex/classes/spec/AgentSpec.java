package com.re.paas.apps.rex.classes.spec;

public class AgentSpec {

	Long id;
	Long agentOrganization;
	Boolean isActive;
	Integer yearsOfExperience;
	
	public Long getId() {
		return id;
	}

	public AgentSpec setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getAgentOrganization() {
		return agentOrganization;
	}

	public AgentSpec setAgentOrganization(Long agentOrganization) {
		this.agentOrganization = agentOrganization;
		return this;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public AgentSpec setIsActive(Boolean isActive) {
		this.isActive = isActive;
		return this;
	}

	public Integer getYearsOfExperience() {
		return yearsOfExperience;
	}

	public AgentSpec setYearsOfExperience(Integer yearsOfExperience) {
		this.yearsOfExperience = yearsOfExperience;
		return this;
	}

}
