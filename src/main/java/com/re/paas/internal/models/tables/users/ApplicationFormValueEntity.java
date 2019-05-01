package com.re.paas.internal.models.tables.users;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;

import nonapi.io.github.classgraph.json.Id;

@Entity
@Cache
public class ApplicationFormValueEntity {

	@Id
	Long id;
	@IndexDescriptor
	String fieldId;
	@IndexDescriptor
	String applicationId;
	String value;
	Date dateUpdated;

	public Long getId() {
		return id;
	}

	public ApplicationFormValueEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public String getFieldId() {
		return fieldId;
	}

	public ApplicationFormValueEntity setFieldId(String fieldId) {
		this.fieldId = fieldId;
		return this;
	}

	public Long getApplicationId() {
		return Long.parseLong(applicationId);
	}

	public ApplicationFormValueEntity setApplicationId(Long applicationId) {
		this.applicationId = applicationId.toString();
		return this;
	}

	public String getValue() {
		return value;
	}

	public ApplicationFormValueEntity setValue(String value) {
		this.value = value;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public ApplicationFormValueEntity setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}
}
