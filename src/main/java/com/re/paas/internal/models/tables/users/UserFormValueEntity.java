package com.re.paas.internal.models.tables.users;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;

import nonapi.io.github.classgraph.json.Id;

@Cache
@Entity
public class UserFormValueEntity {

	@Id
	Long id;
	@IndexDescriptor
	String fieldId;
	@IndexDescriptor
	Integer userId;
	String value;
	Integer dateCreated;
	Integer dateUpdated;

	public Long getId() {
		return id;
	}

	public UserFormValueEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public String getFieldId() {
		return fieldId;
	}

	public UserFormValueEntity setFieldId(String fieldId) {
		this.fieldId = fieldId;
		return this;
	}

	public String getValue() {
		return value;
	}

	public UserFormValueEntity setValue(String value) {
		this.value = value;
		return this;
	}

	public Integer getDateCreated() {
		return dateCreated;
	}

	public UserFormValueEntity setDateCreated(Integer dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Integer getDateUpdated() {
		return dateUpdated;
	}

	public UserFormValueEntity setDateUpdated(Integer dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

	public Integer getUserId() {
		return userId;
	}

	public UserFormValueEntity setUserId(Integer userId) {
		this.userId = userId;
		return this;
	}

}
