package com.re.paas.apps.rex.classes.spec;

import java.util.Date;

public class PropertyViewRequestHistorySpec {

	Long id;

	Long requestId;
	
	Long agentId;
	
	Integer status;
	
	String statusMessage;
	
	Date dateCreated;
	
	public Long getId() {
		return id;
	}

	public PropertyViewRequestHistorySpec setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getRequestId() {
		return requestId;
	}

	public PropertyViewRequestHistorySpec setRequestId(Long requestId) {
		this.requestId = requestId;
		return this;
	}

	public Long getAgentId() {
		return agentId;
	}

	public PropertyViewRequestHistorySpec setAgentId(Long agentId) {
		this.agentId = agentId;
		return this;
	}

	public Integer getStatus() {
		return status;
	}

	public PropertyViewRequestHistorySpec setStatus(Integer status) {
		this.status = status;
		return this;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public PropertyViewRequestHistorySpec setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public PropertyViewRequestHistorySpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
}
