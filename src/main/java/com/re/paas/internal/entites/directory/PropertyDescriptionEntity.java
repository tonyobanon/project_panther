package com.re.paas.internal.entites.directory;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Cache
@Entity
public class PropertyDescriptionEntity {
	
	@Id
	Long id;
	
	String description;
	Date dateCreated;

	public Long getId() {
		return id;
	}

	public PropertyDescriptionEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public PropertyDescriptionEntity setDescription(String description) {
		this.description = description;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public PropertyDescriptionEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
}
