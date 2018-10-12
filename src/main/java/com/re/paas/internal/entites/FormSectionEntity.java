package com.re.paas.internal.entites;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.IfNotNull;
import com.re.paas.api.classes.ClientRBRef;

@Cache
@Entity
public class FormSectionEntity {

	@Id
	String id;
	@Index
	ClientRBRef title;
	@Index
	Integer type;
	ClientRBRef description;
	@Index(IfNotNull.class)
	String realm;

	public String getId() {
		return id;
	}

	public FormSectionEntity setId(String id) {
		this.id = id;
		return this;
	}

	public ClientRBRef getTitle() {
		return title;
	}

	public FormSectionEntity setTitle(ClientRBRef title) {
		this.title = title;
		return this;
	}

	public Integer getType() {
		return type;
	}

	public FormSectionEntity setType(Integer type) {
		this.type = type;
		return this;
	}

	public String getRealm() {
		return realm;
	}

	public FormSectionEntity setRealm(String realm) {
		this.realm = realm;
		return this;
	}

	public ClientRBRef getDescription() {
		return description;
	}

	public FormSectionEntity setDescription(ClientRBRef description) {
		this.description = description;
		return this;
	}
}
