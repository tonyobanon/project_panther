package com.re.paas.internal.entites.directory;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Cache
@Entity
public class AgentOrganizationReviewEntity {

	@Id
	Long id;
	
	@Index
	String agentOrganization;

	@Index
	String userId;
	
	String description;
	
	@Index
	Integer rating;
	
	Date dateCreated;
	
	public Long getId() {
		return id;
	}

	public AgentOrganizationReviewEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getAgentOrganization() {
		return Long.parseLong(agentOrganization);
	}

	public AgentOrganizationReviewEntity setAgentOrganization(Long agentOrganization) {
		this.agentOrganization = agentOrganization.toString();
		return this;
	}
	
	public Long getUserId() {
		return Long.parseLong(userId);
	}

	public AgentOrganizationReviewEntity setUserId(Long userId) {
		this.userId = userId.toString();
		return this;
	}

	public String getDescription() {
		return description;
	}

	public AgentOrganizationReviewEntity setDescription(String description) {
		this.description = description;
		return this;
	}

	public Integer getRating() {
		return rating;
	}

	public AgentOrganizationReviewEntity setRating(Integer rating) {
		this.rating = rating;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public AgentOrganizationReviewEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
	
}
