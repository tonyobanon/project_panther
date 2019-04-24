package com.re.paas.internal.classes.spec;

import java.util.Date;

import com.re.paas.api.classes.IndexedNameSpec;
import com.re.paas.internal.classes.ApplicationStatus;

public class BaseApplicationSpec {
	
	private Long id;
	private String role;
	private ApplicationStatus status;
	
	private String name;
	private IndexedNameSpec nameSpec;
	
	private Date dateCreated;
	private Date dateUpdated;

	public Long getId() {
		return id;
	}

	public BaseApplicationSpec setId(Long id) {
		this.id = id;
		return this;
	} 
	
	public String getRole() {
		return role;
	}

	public BaseApplicationSpec setRole(String role) {
		this.role = role;
		return this;
	}

	public ApplicationStatus getStatus() {
		return status;
	}

	public BaseApplicationSpec setStatus(ApplicationStatus status) {
		this.status = status;
		return this;
	}
	
	public IndexedNameSpec getNameSpec() {
		return nameSpec;
	}

	public BaseApplicationSpec setNameSpec(IndexedNameSpec nameSpec) {
		this.nameSpec = nameSpec;
		this.name = nameSpec.toString();
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public BaseApplicationSpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public BaseApplicationSpec setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

	public String getName() {
		return name;
	}

	public BaseApplicationSpec setName(String name) {
		this.name = name;
		return this;
	}

}
