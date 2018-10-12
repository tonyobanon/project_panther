package com.re.paas.internal.entites.directory;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Cache
@Entity
public class PropertyViewRequestHistoryEntity {

	@Id
	Long id;

	@Index
	String requestId;
	
	String agentId;
	
	Integer status;
	
	String statusMessage;
	
	Date dateCreated;
	
	public Long getId() {
		return id;
	}

	public PropertyViewRequestHistoryEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getRequestId() {
		return Long.parseLong(requestId);
	}

	public PropertyViewRequestHistoryEntity setRequestId(Long requestId) {
		this.requestId = requestId.toString();
		return this;
	}

	public Long getAgentId() {
		return Long.parseLong(agentId);
	}

	public PropertyViewRequestHistoryEntity setAgentId(Long agentId) {
		this.agentId = agentId.toString();
		return this;
	}

	public Integer getStatus() {
		return status;
	}

	public PropertyViewRequestHistoryEntity setStatus(Integer status) {
		this.status = status;
		return this;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public PropertyViewRequestHistoryEntity setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public PropertyViewRequestHistoryEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
}
