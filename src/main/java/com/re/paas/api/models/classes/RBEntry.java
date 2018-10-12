package com.re.paas.api.models.classes;

import java.util.Date;

public class RBEntry {

	String key;
	String locale;
	String value;
	
	Date dateCreated;
	
	public RBEntry(String key, String locale, String value) {
		this.key = key;
		this.locale = locale;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public RBEntry setKey(String key) {
		this.key = key;
		return this;
	}

	public String getLocale() {
		return locale;
	}

	public RBEntry setLocale(String locale) {
		this.locale = locale;
		return this;
	}

	public String getValue() {
		return value;
	}

	public RBEntry setValue(String value) {
		this.value = value;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public RBEntry setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
	
}
