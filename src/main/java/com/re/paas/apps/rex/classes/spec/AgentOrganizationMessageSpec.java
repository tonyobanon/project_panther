package com.re.paas.apps.rex.classes.spec;

import java.util.Date;
import java.util.Map;

public class AgentOrganizationMessageSpec {

	Long id;

	Long agentOrganization;

	Boolean isRead;

	Long userId;
	
	String name;

	String email;

	String mobile;

	String message;
	
	IssueResolution resolution;
	
	Map<Integer, Long> resolutionHistory;
	
	Date dateCreated;

	public Long getId() {
		return id;
	}

	public AgentOrganizationMessageSpec setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getAgentOrganization() {
		return agentOrganization;
	}

	public AgentOrganizationMessageSpec setAgentOrganization(Long agentOrganization) {
		this.agentOrganization = agentOrganization;
		return this;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public AgentOrganizationMessageSpec setIsRead(Boolean isRead) {
		this.isRead = isRead;
		return this;
	}

	public Long getUserId() {
		return userId;
	}

	public AgentOrganizationMessageSpec setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	public String getName() {
		return name;
	}

	public AgentOrganizationMessageSpec setName(String name) {
		this.name = name;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public AgentOrganizationMessageSpec setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getMobile() {
		return mobile;
	}

	public AgentOrganizationMessageSpec setMobile(String mobile) {
		this.mobile = mobile;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public AgentOrganizationMessageSpec setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public IssueResolution getResolution() {
		return resolution;
	}

	public AgentOrganizationMessageSpec setResolution(IssueResolution resolution) {
		this.resolution = resolution;
		return this;
	}

	public Map<Integer, Long> getResolutionHistory() {
		return resolutionHistory;
	}

	public AgentOrganizationMessageSpec setResolutionHistory(Map<Integer, Long> resolutionHistory) {
		this.resolutionHistory = resolutionHistory;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public AgentOrganizationMessageSpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
}
