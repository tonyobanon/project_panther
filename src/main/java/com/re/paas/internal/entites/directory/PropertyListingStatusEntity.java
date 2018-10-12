package com.re.paas.internal.entites.directory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.re.paas.apps.rex.classes.spec.FileAttachmentRef;

@Cache
@Entity
public class PropertyListingStatusEntity {

	@Id
	Long id;
	String propertyId;
	Long principal;
	Integer listingStatus;
	String message;
	List<FileAttachmentRef> attachments;
	Date dateCreated;
	
	public PropertyListingStatusEntity() {
		this.attachments = new ArrayList<FileAttachmentRef>();
	}

	public Long getId() {
		return id;
	}

	public PropertyListingStatusEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getPropertyId() {
		return Long.parseLong(propertyId);
	}

	public PropertyListingStatusEntity setPropertyId(Long propertyId) {
		this.propertyId = propertyId.toString();
		return this;
	}

	public Long getPrincipal() {
		return principal;
	}

	public PropertyListingStatusEntity setPrincipal(Long principal) {
		this.principal = principal;
		return this;
	}

	public Integer getListingStatus() {
		return listingStatus;
	}

	public PropertyListingStatusEntity setListingStatus(Integer listingStatus) {
		this.listingStatus = listingStatus;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public PropertyListingStatusEntity setMessage(String message) {
		this.message = message;
		return this;
	}

	public List<FileAttachmentRef> getAttachments() {
		return attachments;
	}

	public PropertyListingStatusEntity setAttachments(List<FileAttachmentRef> attachments) {
		this.attachments = attachments;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public PropertyListingStatusEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
}
