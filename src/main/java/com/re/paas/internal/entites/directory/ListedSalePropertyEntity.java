package com.re.paas.internal.entites.directory;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Subclass;

@Cache
@Subclass(index=true)
public class ListedSalePropertyEntity extends ListedPropertyEntity {

	@Index
	Integer paymentOption;

	public Integer getPaymentOption() {
		return paymentOption;
	}

	public ListedSalePropertyEntity setPaymentOption(Integer paymentOption) {
		this.paymentOption = paymentOption;
		return this;
	}

	public Long getId() {
		return id;
	}

	public ListedSalePropertyEntity setId(Long id) {
		this.id = id;
		return this;
	}
	
	public Long getPropertyId() {
		return propertyId;
	}

	public ListedSalePropertyEntity setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
		return this;
	}
	
	public Long getAgentOrganizationId() {
		return agentOrganizationId;
	}

	public ListedSalePropertyEntity setAgentOrganizationId(Long agentOrganizationId) {
		this.agentOrganizationId = agentOrganizationId;
		return this;
	}
	
	public Boolean getIsPremium() {
		return isPremium;
	}

	public ListedSalePropertyEntity setIsPremium(Boolean isPremium) {
		this.isPremium = isPremium;
		return this;
	}

	public Boolean getIsHot() {
		return isHot;
	}

	public ListedSalePropertyEntity setIsHot(Boolean isHot) {
		this.isHot = isHot;
		return this;
	}

	public Integer getAvailabilityStatus() {
		return availabilityStatus;
	}

	public ListedSalePropertyEntity setAvailabilityStatus(Integer availabilityStatus) {
		this.availabilityStatus = availabilityStatus;
		return this;
	}
	
	public Date getDateCreated() {
		return dateCreated;
	}

	public ListedSalePropertyEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public ListedSalePropertyEntity setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

}
