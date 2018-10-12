package com.re.paas.internal.entites.directory;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Subclass;

@Cache
@Subclass(index=true)
public class ListedRentPropertyEntity extends ListedPropertyEntity {

	@Index
	Integer yearsRequired;

	public Integer getYearsRequired() {
		return yearsRequired;
	}

	public ListedRentPropertyEntity setYearsRequired(Integer yearsRequired) {
		this.yearsRequired = yearsRequired;
		return this;
	}

	public Long getId() {
		return id;
	}

	public ListedRentPropertyEntity setId(Long id) {
		this.id = id;
		return this;
	}
	
	public Long getPropertyId() {
		return propertyId;
	}

	public ListedRentPropertyEntity setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
		return this;
	}
	public Long getAgentOrganizationId() {
		return agentOrganizationId;
	}

	public ListedRentPropertyEntity setAgentOrganizationId(Long agentOrganizationId) {
		this.agentOrganizationId = agentOrganizationId;
		return this;
	}
	
	public Boolean getIsPremium() {
		return isPremium;
	}

	public ListedRentPropertyEntity setIsPremium(Boolean isPremium) {
		this.isPremium = isPremium;
		return this;
	}

	public Boolean getIsHot() {
		return isHot;
	}

	public ListedRentPropertyEntity setIsHot(Boolean isHot) {
		this.isHot = isHot;
		return this;
	}

	public Integer getAvailabilityStatus() {
		return availabilityStatus;
	}

	public ListedRentPropertyEntity setAvailabilityStatus(Integer availabilityStatus) {
		this.availabilityStatus = availabilityStatus;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public ListedRentPropertyEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public ListedRentPropertyEntity setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}
	
}
