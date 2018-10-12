package com.re.paas.internal.entites.directory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Cache
@Entity
public class UserSavedListEntity {

	@Id
	Long id;
	List<Long> properties;
	Date dateUpdated;
	
	public UserSavedListEntity() {
		this.properties = new ArrayList<Long>();
	}

	public Long getId() {
		return id;
	}

	public UserSavedListEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public List<Long> getProperties() {
		return properties;
	}

	public UserSavedListEntity setProperties(List<Long> properties) {
		this.properties = properties;
		return this;
	}
	
	public UserSavedListEntity addProperty(Long property) {
		this.properties.add(property);
		return this;
	}
	
	public UserSavedListEntity removeProperty(Long property) {
		this.properties.remove(property);
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public UserSavedListEntity setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}
	
}
