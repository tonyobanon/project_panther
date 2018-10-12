package com.re.paas.apps.rex.classes.spec;

import java.util.Date;

public class AgentOrganizationReviewSpec {

	Long id;
	Long agentOrganization;
	Long userId;
	String description;
	Integer rating;
	Date dateCreated;
	
	public Long getId() {
		return id;
	}

	public AgentOrganizationReviewSpec setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getAgentOrganization() {
		return agentOrganization;
	}

	public AgentOrganizationReviewSpec setAgentOrganization(Long agentOrganization) {
		this.agentOrganization = agentOrganization;
		return this;
	}

	public Long getUserId() {
		return userId;
	}

	public AgentOrganizationReviewSpec setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public AgentOrganizationReviewSpec setDescription(String description) {
		this.description = description;
		return this;
	}

	public Integer getRating() {
		return rating;
	}

	public AgentOrganizationReviewSpec setRating(Integer rating) {
		this.rating = rating;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public AgentOrganizationReviewSpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
	
}
