package com.re.paas.internal.entites.directory;

import java.util.Date;
import java.util.Map;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Cache
@Entity
public class AgentAvailabilityScheduleEntity {

	@Id
	String id;
	@Index
	String agent;
	Map<String, String> baseSchedules;
	Date dateUpdated;
	
	public String getId() {
		return id;
	}

	public AgentAvailabilityScheduleEntity setId(String id) {
		this.id = id;
		return this;
	}

	public Long getAgent() {
		return Long.parseLong(agent);
	}

	public AgentAvailabilityScheduleEntity setAgent(Long agent) {
		this.agent = agent.toString();
		return this;
	}

	public Map<String, String> getBaseSchedules() {
		return baseSchedules;
	}

	public AgentAvailabilityScheduleEntity setBaseSchedules(Map<String, String> baseSchedules) {
		this.baseSchedules = baseSchedules;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public AgentAvailabilityScheduleEntity setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

}
