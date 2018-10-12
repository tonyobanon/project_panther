package com.re.paas.internal.classes.spec;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserSavedListSpec {

	Long id;
	List<Long> properties;
	Date dateUpdated;
	
	public UserSavedListSpec() {
		this.properties = new ArrayList<Long>();
	}

	public Long getId() {
		return id;
	}

	public UserSavedListSpec setId(Long id) {
		this.id = id;
		return this;
	}

	public List<Long> getProperties() {
		return properties;
	}

	public UserSavedListSpec setProperties(List<Long> properties) {
		this.properties = properties;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public UserSavedListSpec setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}
	
}
