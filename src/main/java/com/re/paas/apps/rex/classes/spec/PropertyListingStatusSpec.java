package com.re.paas.apps.rex.classes.spec;

import java.util.Date;
import java.util.List;

public class PropertyListingStatusSpec {

	Long id;
	Long propertyId;
	Long principal;
	PropertyListingStatus listingStatus;
	String message;
	List<FileAttachmentRef> attachments;
	Date dateCreated;

	public Long getId() {
		return id;
	}

	public PropertyListingStatusSpec setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getPropertyId() {
		return propertyId;
	}

	public PropertyListingStatusSpec setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
		return this;
	}

	public Long getPrincipal() {
		return principal;
	}

	public PropertyListingStatusSpec setPrincipal(Long principal) {
		this.principal = principal;
		return this;
	}

	public PropertyListingStatus getListingStatus() {
		return listingStatus;
	}

	public PropertyListingStatusSpec setListingStatus(PropertyListingStatus listingStatus) {
		this.listingStatus = listingStatus;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public PropertyListingStatusSpec setMessage(String message) {
		this.message = message;
		return this;
	}

	public List<FileAttachmentRef> getAttachments() {
		return attachments;
	}

	public PropertyListingStatusSpec setAttachments(List<FileAttachmentRef> attachments) {
		this.attachments = attachments;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public PropertyListingStatusSpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
}
