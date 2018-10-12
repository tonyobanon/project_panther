package com.re.paas.internal.entites.directory;

import java.util.Date;
import java.util.Map;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Cache
@Entity
public class AgentOrganizationAvailabilityScheduleEntity {

	@Id
	String id;
	@Index
	String agentOrganization;
	Map<String, String> baseSchedules;
	Date dateUpdated;

	public String getId() {
		return id;
	}

	public AgentOrganizationAvailabilityScheduleEntity setId(String id) {
		this.id = id;
		return this;
	}

	public Long getAgentOrganization() {
		return Long.parseLong(agentOrganization);
	}

	public AgentOrganizationAvailabilityScheduleEntity setAgentOrganization(Long agentOrganization) {
		this.agentOrganization = agentOrganization.toString();
		return this;
	}

	public Map<String, String> getBaseSchedules() {
		return baseSchedules;
	}

	public AgentOrganizationAvailabilityScheduleEntity setBaseSchedules(Map<String, String> baseSchedules) {
		this.baseSchedules = baseSchedules;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public AgentOrganizationAvailabilityScheduleEntity setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

}
