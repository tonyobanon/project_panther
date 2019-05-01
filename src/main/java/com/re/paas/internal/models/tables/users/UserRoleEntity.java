package com.re.paas.internal.models.tables.users;

import java.util.Date;
import java.util.List;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.condition.IfTrue;
import com.re.paas.api.classes.FluentArrayList;

import nonapi.io.github.classgraph.json.Id;

@Cache
@Entity
public class UserRoleEntity {

	@Id
	String name;
	List<String> spec;
	@IndexDescriptor(IfTrue.class) boolean isDefault;
	@IndexDescriptor String realm;
	Date dateCreated;

	public UserRoleEntity() {
		this.spec = new FluentArrayList<>();
	}
	
	public String getName() {
		return name;
	}

	public UserRoleEntity setName(String name) {
		this.name = name;
		return this;
	}

	public List<String> getSpec() {
		return spec;
	}

	public UserRoleEntity setSpec(List<String> spec) {
		this.spec = spec;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public UserRoleEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public String getRealm() {
		return realm;
	}

	public UserRoleEntity setRealm(String realm) {
		this.realm = realm;
		return this;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public UserRoleEntity setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
		return this;
	}
}
