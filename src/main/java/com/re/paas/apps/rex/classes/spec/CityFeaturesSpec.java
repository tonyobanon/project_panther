package com.re.paas.apps.rex.classes.spec;

import java.util.Date;

public class CityFeaturesSpec {

	Integer id;

	Integer water;
	Integer goodRoad;
	Integer security;
	Integer socialization;
	Integer power;

	Date dateCreated;
	
	public Integer getId() {
		return id;
	}

	public CityFeaturesSpec setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getWater() {
		return water;
	}

	public CityFeaturesSpec setWater(Integer water) {
		this.water = water;
		return this;
	}

	public Integer getGoodRoad() {
		return goodRoad;
	}

	public CityFeaturesSpec setGoodRoad(Integer goodRoad) {
		this.goodRoad = goodRoad;
		return this;
	}

	public Integer getSecurity() {
		return security;
	}

	public CityFeaturesSpec setSecurity(Integer security) {
		this.security = security;
		return this;
	}

	public Integer getSocialization() {
		return socialization;
	}

	public CityFeaturesSpec setSocialization(Integer socialization) {
		this.socialization = socialization;
		return this;
	}

	public Integer getPower() {
		return power;
	}

	public CityFeaturesSpec setPower(Integer power) {
		this.power = power;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public CityFeaturesSpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
}
