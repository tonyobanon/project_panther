package com.re.paas.apps.rex.classes.spec;

import java.util.Date;

public class BaseAgentOrganizationMessageSpec {

	Long id;

	Boolean isRead;

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

	public BaseAgentOrganizationMessageSpec setId(Long id) {
		this.id = id;
		return this;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public BaseAgentOrganizationMessageSpec setIsRead(Boolean isRead) {
		this.isRead = isRead;
		return this;
	}

	public String getName() {
		return name;
	}

	public BaseAgentOrganizationMessageSpec setName(String name) {
		this.name = name;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public BaseAgentOrganizationMessageSpec setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getMobile() {
		return mobile;
	}

	public BaseAgentOrganizationMessageSpec setMobile(String mobile) {
		this.mobile = mobile;
		return this;
	}

	public IssueResolution getResolution() {
		return resolution;
	}

	public BaseAgentOrganizationMessageSpec setResolution(IssueResolution resolution) {
		this.resolution = resolution;
		return this;
	}
	
	public String getTruncatedMessage() {
		return truncatedMessage;
	}

	public BaseAgentOrganizationMessageSpec setTruncatedMessage(String truncatedMessage) {
		this.truncatedMessage = truncatedMessage;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public BaseAgentOrganizationMessageSpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public BaseAgentOrganizationMessageSpec setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}
}
