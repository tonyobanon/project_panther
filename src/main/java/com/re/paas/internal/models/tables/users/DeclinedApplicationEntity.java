package com.re.paas.internal.models.tables.users;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;

import nonapi.io.github.classgraph.json.Id;

@Cache
@Entity
public class DeclinedApplicationEntity {

	@Id Long applicationId;

	Long staffId;
	Integer reason;

	Date dateCreated;

	public Long getApplicationId() {
		return applicationId;
	}

	public DeclinedApplicationEntity setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
		return this;
	}

	public Long getStaffId() {
		return staffId;
	}

	public DeclinedApplicationEntity setStaffId(Long staffId) {
		this.staffId = staffId;
		return this;
	}

	public Integer getReason() {
		return reason;
	}

	public DeclinedApplicationEntity setReason(Integer reason) {
		this.reason = reason;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public DeclinedApplicationEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
	
}
