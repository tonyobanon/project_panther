package com.re.paas.internal.entites;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Cache
@Entity
public class RBEntryEntity {

	@Id
	String key;
	
	@Index
	String locale;
	
	String value;
	
	Date dateCreated;

	public String getKey() {
		return key;
	}

	public RBEntryEntity setKey(String key) {
		this.key = key;
		return this;
	}

	public String getLocale() {
		return locale;
	}

	public RBEntryEntity setLocale(String locale) {
		this.locale = locale;
		return this;
	}

	public String getValue() {
		return value;
	}

	public RBEntryEntity setValue(String value) {
		this.value = value;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public RBEntryEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
	
}
