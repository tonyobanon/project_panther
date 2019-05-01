package com.re.paas.internal.models.tables.users;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.condition.IfNotEmpty;

import nonapi.io.github.classgraph.json.Id;

@Entity
@Cache
public class ApplicationEntity {

	@Id
	Long id;
	@IndexDescriptor
	String role;
	@IndexDescriptor
	String status;
	@IndexDescriptor(IfNotEmpty.class)
	String ref;
	Date dateCreated;
	Date dateUpdated;

	public Long getId() {
		return id;
	}

	public ApplicationEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public String getRole() {
		return role;
	}

	public ApplicationEntity setRole(String role) {
		this.role = role;
		return this;
	}

	public Integer getStatus() {
		return Integer.parseInt(status);
	}

	public ApplicationEntity setStatus(Integer status) {
		this.status = status.toString();
		return this;
	}

	public String getRef() {
		return ref;
	}

	public ApplicationEntity setRef(String ref) {
		this.ref = ref;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public ApplicationEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public ApplicationEntity setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}
}
