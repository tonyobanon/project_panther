package com.re.paas.internal.entites.directory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Stringify;
import com.re.paas.gae_adapter.classes.IntStringifier;

@Cache
@Entity
public class AgentOrganizationWhistleblowMessageEntity {

	@Id
	Long id;

	String agentOrganization;

	Boolean isRead;

	Long userId;

	String name;

	String email;

	String mobile;

	String message;

	@Index
	Integer resolution;
	
	@Stringify(value = IntStringifier.class)
	Map<Integer, Long> resolutionHistory;

	Date dateCreated;

	Date dateUpdated;

	public Long getId() {
		return id;
	}

	public AgentOrganizationWhistleblowMessageEntity setId(Long id) {
		this.id = id;
		this.resolutionHistory = new HashMap<Integer, Long>();
		return this;
	}

	public Long getAgentOrganization() {
		return Long.parseLong(agentOrganization);
	}

	public AgentOrganizationWhistleblowMessageEntity setAgentOrganization(Long agentOrganization) {
		this.agentOrganization = agentOrganization.toString();
		return this;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public AgentOrganizationWhistleblowMessageEntity setIsRead(Boolean isRead) {
		this.isRead = isRead;
		return this;
	}

	public Long getUserId() {
		return userId;
	}

	public AgentOrganizationWhistleblowMessageEntity setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	public String getName() {
		return name;
	}

	public AgentOrganizationWhistleblowMessageEntity setName(String name) {
		this.name = name;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public AgentOrganizationWhistleblowMessageEntity setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getMobile() {
		return mobile;
	}

	public AgentOrganizationWhistleblowMessageEntity setMobile(String mobile) {
		this.mobile = mobile;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public AgentOrganizationWhistleblowMessageEntity setMessage(String message) {
		this.message = message;
		return this;
	}

	public Integer getResolution() {
		return resolution;
	}

	public AgentOrganizationWhistleblowMessageEntity setResolution(Integer resolution) {
		this.resolution = resolution;
		return this;
	}

	public Map<Integer, Long> getResolutionHistory() {
		return resolutionHistory;
	}

	public AgentOrganizationWhistleblowMessageEntity setResolutionHistory(Map<Integer, Long> resolutionHistory) {
		this.resolutionHistory = resolutionHistory;
		return this;
	}

	public AgentOrganizationWhistleblowMessageEntity addResolutionHistory(Integer resolution, Long principal) {
		this.resolutionHistory.put(resolution, principal);
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public AgentOrganizationWhistleblowMessageEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public AgentOrganizationWhistleblowMessageEntity setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

}
