package com.re.paas.internal.entites;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.re.paas.api.classes.ClientRBRef;

@Cache
@Entity
public class FormSimpleFieldEntity {

	@Id
	String id;
	ClientRBRef title;
	@Index String section;
	String inputType;
	String context;
	
	Integer sortOrder;
	String defaultValue;
	Boolean isVisible;
	Boolean isRequired;
	Boolean isDefault;
	
	Date dateCreated;

	
	public String getId() {
		return id;
	}

	public FormSimpleFieldEntity setId(String id) {
		this.id = id;
		return this;
	}

	public ClientRBRef getTitle() {
		return title;
	}

	public FormSimpleFieldEntity setTitle(ClientRBRef title) {
		this.title = title;
		return this;
	}

	public String getSection() {
		return section;
	}

	public FormSimpleFieldEntity setSection(String section) {
		this.section = section;
		return this;
	}

	public String getInputType() {
		return inputType;
	}

	public FormSimpleFieldEntity setInputType(String inputType) {
		this.inputType = inputType;
		return this;
	}
	
	public String getContext() {
		return context;
	}

	public FormSimpleFieldEntity setContext(String context) {
		this.context = context;
		return this;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public FormSimpleFieldEntity setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public FormSimpleFieldEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public FormSimpleFieldEntity setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
		return this;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public FormSimpleFieldEntity setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	public Boolean getIsVisible() {
		return isVisible;
	}

	public FormSimpleFieldEntity setIsVisible(Boolean isVisible) {
		this.isVisible = isVisible;
		return this;
	}

	public Boolean getIsRequired() {
		return isRequired;
	}

	public FormSimpleFieldEntity setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
		return this;
	}
}
