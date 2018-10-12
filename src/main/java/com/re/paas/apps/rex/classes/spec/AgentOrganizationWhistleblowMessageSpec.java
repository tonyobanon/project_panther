package com.re.paas.apps.rex.classes.spec;

import java.util.Date;
import java.util.Map;

public class AgentOrganizationWhistleblowMessageSpec {

	Long id;

	Long agentOrganization;

	String agentOrganizationName;
	
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

	public AgentOrganizationWhistleblowMessageSpec setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getAgentOrganization() {
		return agentOrganization;
	}

	public AgentOrganizationWhistleblowMessageSpec setAgentOrganization(Long agentOrganization) {
		this.agentOrganization = agentOrganization;
		return this;
	}

	public String getAgentOrganizationName() {
		return agentOrganizationName;
	}

	public AgentOrganizationWhistleblowMessageSpec setAgentOrganizationName(String agentOrganizationName) {
		this.agentOrganizationName = agentOrganizationName;
		return this;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public AgentOrganizationWhistleblowMessageSpec setIsRead(Boolean isRead) {
		this.isRead = isRead;
		return this;
	}

	public Long getUserId() {
		return userId;
	}

	public AgentOrganizationWhistleblowMessageSpec setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	public String getName() {
		return name;
	}

	public AgentOrganizationWhistleblowMessageSpec setName(String name) {
		this.name = name;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public AgentOrganizationWhistleblowMessageSpec setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getMobile() {
		return mobile;
	}

	public AgentOrganizationWhistleblowMessageSpec setMobile(String mobile) {
		this.mobile = mobile;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public AgentOrganizationWhistleblowMessageSpec setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public IssueResolution getResolution() {
		return resolution;
	}

	public AgentOrganizationWhistleblowMessageSpec setResolution(IssueResolution resolution) {
		this.resolution = resolution;
		return this;
	}

	public Map<Integer, Long> getResolutionHistory() {
		return resolutionHistory;
	}

	public AgentOrganizationWhistleblowMessageSpec setResolutionHistory(Map<Integer, Long> resolutionHistory) {
		this.resolutionHistory = resolutionHistory;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public AgentOrganizationWhistleblowMessageSpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
}
