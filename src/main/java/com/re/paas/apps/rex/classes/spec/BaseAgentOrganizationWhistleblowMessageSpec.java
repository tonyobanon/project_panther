package com.re.paas.apps.rex.classes.spec;

import java.util.Date;

public class BaseAgentOrganizationWhistleblowMessageSpec {

	Long id;

	Boolean isRead;
	
	Long agentOrganization;
	
	String agentOrganizationName;

	String name;

	String email;

	String mobile;

	IssueResolution resolution;

	String truncatedMessage;
	
	Date dateCreated;

	Date dateUpdated;

	public Long getId() {
		return id;
	}

	public BaseAgentOrganizationWhistleblowMessageSpec setId(Long id) {
		this.id = id;
		return this;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public BaseAgentOrganizationWhistleblowMessageSpec setIsRead(Boolean isRead) {
		this.isRead = isRead;
		return this;
	}
	
	public Long getAgentOrganization() {
		return agentOrganization;
	}

	public BaseAgentOrganizationWhistleblowMessageSpec setAgentOrganization(Long agentOrganization) {
		this.agentOrganization = agentOrganization;
		return this;
	}

	public String getAgentOrganizationName() {
		return agentOrganizationName;
	}

	public BaseAgentOrganizationWhistleblowMessageSpec setAgentOrganizationName(String agentOrganizationName) {
		this.agentOrganizationName = agentOrganizationName;
		return this;
	}

	public String getName() {
		return name;
	}

	public BaseAgentOrganizationWhistleblowMessageSpec setName(String name) {
		this.name = name;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public BaseAgentOrganizationWhistleblowMessageSpec setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getMobile() {
		return mobile;
	}

	public BaseAgentOrganizationWhistleblowMessageSpec setMobile(String mobile) {
		this.mobile = mobile;
		return this;
	}

	public IssueResolution getResolution() {
		return resolution;
	}

	public BaseAgentOrganizationWhistleblowMessageSpec setResolution(IssueResolution resolution) {
		this.resolution = resolution;
		return this;
	}
	
	public String getTruncatedMessage() {
		return truncatedMessage;
	}

	public BaseAgentOrganizationWhistleblowMessageSpec setTruncatedMessage(String truncatedMessage) {
		this.truncatedMessage = truncatedMessage;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public BaseAgentOrganizationWhistleblowMessageSpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public BaseAgentOrganizationWhistleblowMessageSpec setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}
}
