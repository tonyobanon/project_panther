package com.re.paas.internal.entites.directory;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Cache
@Entity
public class CityPropertyValuationEntity {

	@Id
	String id;

	Integer averagePriceMin;
	Integer averagePriceMax;
	
	Date dateCreated;
	
	public Integer getId() {
		return Integer.parseInt(id);
	}

	public CityPropertyValuationEntity setId(Integer id) {
		this.id = id.toString();
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public CityPropertyValuationEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
	
}
