package com.re.paas.apps.rex.classes.spec;

import java.util.Date;

public class PropertyPOISpec {

	Long id;

	String school;
	String restaurant;
	String bank;
	String publicTransportation;

	Date dateCreated;
	
	public Long getId() {
		return id;
	}

	public PropertyPOISpec setId(Long id) {
		this.id = id;
		return this;
	}

	public String getSchool() {
		return school;
	}

	public PropertyPOISpec setSchool(String school) {
		this.school = school;
		return this;
	}

	public String getRestaurant() {
		return restaurant;
	}

	public PropertyPOISpec setRestaurant(String restaurant) {
		this.restaurant = restaurant;
		return this;
	}

	public String getBank() {
		return bank;
	}

	public PropertyPOISpec setBank(String bank) {
		this.bank = bank;
		return this;
	}

	public String getPublicTransportation() {
		return publicTransportation;
	}

	public PropertyPOISpec setPublicTransportation(String publicTransportation) {
		this.publicTransportation = publicTransportation;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public PropertyPOISpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
	
}
