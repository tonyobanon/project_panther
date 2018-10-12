package com.re.paas.internal.entites.directory;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.IfNotEmpty;

@Cache
@Entity
public class PropertyViewRequestEntity {

	@Id
	Long id;

	@Index
	String userId;
	
	@Index
	String propertyId;
	
	@Index
	String agentOrganization;
	
	@Index(IfNotEmpty.class)
	String agentId;
	
	Integer status;
	
	String statusMessage;
	
	Date viewingDate;
	
	Date dateCreated;

	Date dateUpdated;
	
	public Long getId() {
		return id;
	}

	public PropertyViewRequestEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getUserId() {
		return Long.parseLong(userId);
	}

	public PropertyViewRequestEntity setUserId(Long userId) {
		this.userId = userId.toString();
		return this;
	}

	public Long getPropertyId() {
		return Long.parseLong(propertyId);
	}

	public PropertyViewRequestEntity setPropertyId(Long propertyId) {
		this.propertyId = propertyId.toString();
		return this;
	}

	public Long getAgentOrganization() {
		return Long.parseLong(agentOrganization);
	}

	public PropertyViewRequestEntity setAgentOrganization(Long agentOrganization) {
		this.agentOrganization = agentOrganization.toString();
		return this;
	}

	public Long getAgentId() {
		return Long.parseLong(agentId);
	}

	public PropertyViewRequestEntity setAgentId(Long agentId) {
		this.agentId = agentId.toString();
		return this;
	}

	public Integer getStatus() {
		return status;
	}

	public PropertyViewRequestEntity setStatus(Integer status) {
		this.status = status;
		return this;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public PropertyViewRequestEntity setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
		return this;
	}

	public Date getViewingDate() {
		return viewingDate;
	}

	public PropertyViewRequestEntity setViewingDate(Date viewingDate) {
		this.viewingDate = viewingDate;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public PropertyViewRequestEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public PropertyViewRequestEntity setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}
	
}
