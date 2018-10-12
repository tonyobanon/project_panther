package com.re.paas.apps.rex.classes.spec;

import java.util.Date;

import com.re.paas.internal.classes.spec.PaymentOptions;

public class ListedSalePropertySpec extends ListedProperty {

	PaymentOptions paymentOption;

	public PaymentOptions getPaymentOption() {
		return paymentOption;
	}

	public ListedSalePropertySpec setPaymentOption(PaymentOptions paymentOption) {
		this.paymentOption = paymentOption;
		return this;
	}

	public Long getId() {
		return id;
	}

	public ListedSalePropertySpec setId(Long id) {
		this.id = id;
		return this;
	}
	
	public Long getPropertyId() {
		return propertyId;
	}

	public ListedSalePropertySpec setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
		return this;
	}

	public Long getAgentOrganizationId() {
		return agentOrganizationId;
	}

	public ListedSalePropertySpec setAgentOrganizationId(Long agentOrganizationId) {
		this.agentOrganizationId = agentOrganizationId;
		return this;
	}
	
	public Boolean getIsPremium() {
		return isPremium;
	}

	public ListedSalePropertySpec setIsPremium(Boolean isPremium) {
		this.isPremium = isPremium;
		return this;
	}

	public Boolean getIsHot() {
		return isHot;
	}

	public ListedSalePropertySpec setIsHot(Boolean isHot) {
		this.isHot = isHot;
		return this;
	}

	public PropertyAvailabilityStatus getAvailabilityStatus() {
		return availabilityStatus;
	}

	public ListedSalePropertySpec setAvailabilityStatus(PropertyAvailabilityStatus availabilityStatus) {
		this.availabilityStatus = availabilityStatus;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public ListedSalePropertySpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public ListedSalePropertySpec setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}
}
