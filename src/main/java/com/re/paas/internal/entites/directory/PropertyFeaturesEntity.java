package com.re.paas.internal.entites.directory;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.re.paas.api.classes.ClientRBRef;

@Cache
@Entity
public class PropertyFeaturesEntity {

	@Id
	Long id;
	@Index
	Integer type;
	ClientRBRef title;
	Date dateCreated;

	public Long getId() {
		return id;
	}

	public PropertyFeaturesEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public Integer getType() {
		return type;
	}

	public PropertyFeaturesEntity setType(Integer type) {
		this.type = type;
		return this;
	}

	public ClientRBRef getTitle() {
		return title;
	}

	public PropertyFeaturesEntity setTitle(ClientRBRef title) {
		this.title = title;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public PropertyFeaturesEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

}
