package com.re.paas.internal.entites.directory;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Cache
@Entity
public class PropertyPOIEntity {

	@Id
	Long id;

	String school;
	String restaurant;
	String bank;
	String publicTransportation;
	
	Date dateCreated;
	
	public Long getId() {
		return id;
	}

	public PropertyPOIEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public String getSchool() {
		return school;
	}

	public PropertyPOIEntity setSchool(String school) {
		this.school = school;
		return this;
	}

	public String getRestaurant() {
		return restaurant;
	}

	public PropertyPOIEntity setRestaurant(String restaurant) {
		this.restaurant = restaurant;
		return this;
	}

	public String getBank() {
		return bank;
	}

	public PropertyPOIEntity setBank(String bank) {
		this.bank = bank;
		return this;
	}

	public String getPublicTransportation() {
		return publicTransportation;
	}

	public PropertyPOIEntity setPublicTransportation(String publicTransportation) {
		this.publicTransportation = publicTransportation;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public PropertyPOIEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

}
