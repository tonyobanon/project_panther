package com.re.paas.internal.entites;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Cache
@Entity
public class MetricEntity {

	@Id String key;
	//@Index 
	String parentKey;
	Integer value;

	public String getKey() {
		return key;
	}

	public MetricEntity setKey(String key) {
		this.key = key;
		return this;
	}

	public Integer getValue() {
		return value;
	}

	public MetricEntity setValue(Integer value) {
		this.value = value;
		return this;
	}

	public String getParentKey() {
		return parentKey;
	}

	public MetricEntity setParentKey(String parentKey) {
		this.parentKey = parentKey;
		return this;
	}
	
}
