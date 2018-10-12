package com.re.paas.apps.rex.classes.spec;

import java.util.Date;

public class PropertyViewRequestSpec {

	Long id;

	Long userId;
	
	Long propertyId;
	
	Long agentOrganization;
	
	Long agentId;
	
	PropertyViewRequestStatus status;
	
	String statusMessage;
	
	Date viewingDate;
	
	Date dateCreated;
	
	Date dateUpdated;

	public Long getId() {
		return id;
	}

	public PropertyViewRequestSpec setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getUserId() {
		return userId;
	}

	public PropertyViewRequestSpec setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	public Long getPropertyId() {
		return propertyId;
	}

	public PropertyViewRequestSpec setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
		return this;
	}

	public Long getAgentOrganization() {
		return agentOrganization;
	}

	public PropertyViewRequestSpec setAgentOrganization(Long agentOrganization) {
		this.agentOrganization = agentOrganization;
		return this;
	}

	public Long getAgentId() {
		return agentId;
	}

	public PropertyViewRequestSpec setAgentId(Long agentId) {
		this.agentId = agentId;
		return this;
	}

	public PropertyViewRequestStatus getStatus() {
		return status;
	}

	public PropertyViewRequestSpec setStatus(PropertyViewRequestStatus status) {
		this.status = status;
		return this;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public PropertyViewRequestSpec setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
		return this;
	}

	public Date getViewingDate() {
		return viewingDate;
	}

	public PropertyViewRequestSpec setViewingDate(Date viewingDate) {
		this.viewingDate = viewingDate;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public PropertyViewRequestSpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public PropertyViewRequestSpec setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}
	
}
