package com.re.paas.apps.rex.classes.spec;

import java.util.Date;

public class ListedRentPropertySpec extends ListedProperty {

	Integer yearsRequired;

	public Integer getYearsRequired() {
		return yearsRequired;
	}

	public ListedRentPropertySpec setYearsRequired(Integer yearsRequired) {
		this.yearsRequired = yearsRequired;
		return this;
	}
	
	public Long getId() {
		return id;
	}

	public ListedRentPropertySpec setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getPropertyId() {
		return propertyId;
	}

	public ListedRentPropertySpec setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
		return this;
	}
	
	public Long getAgentOrganizationId() {
		return agentOrganizationId;
	}

	public ListedRentPropertySpec setAgentOrganizationId(Long agentOrganizationId) {
		this.agentOrganizationId = agentOrganizationId;
		return this;
	}
	
	public Boolean getIsPremium() {
		return isPremium;
	}

	public ListedRentPropertySpec setIsPremium(Boolean isPremium) {
		this.isPremium = isPremium;
		return this;
	}

	public Boolean getIsHot() {
		return isHot;
	}

	public ListedRentPropertySpec setIsHot(Boolean isHot) {
		this.isHot = isHot;
		return this;
	}

	public PropertyAvailabilityStatus getAvailabilityStatus() {
		return availabilityStatus;
	}

	public ListedRentPropertySpec setAvailabilityStatus(PropertyAvailabilityStatus availabilityStatus) {
		this.availabilityStatus = availabilityStatus;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public ListedRentPropertySpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public ListedRentPropertySpec setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}
	
}
