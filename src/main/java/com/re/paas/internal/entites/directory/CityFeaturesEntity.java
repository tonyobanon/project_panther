package com.re.paas.internal.entites.directory;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Cache
@Entity
public class CityFeaturesEntity {
 
	@Id
	String id;

	Integer water;
	Integer goodRoad;
	Integer security;
	Integer socialization;
	Integer power;

	Date dateCreated;
	
	public Integer getId() {
		return Integer.parseInt(id);
	}

	public CityFeaturesEntity setId(Integer id) {
		this.id = id.toString();
		return this;
	}

	public Integer getWater() {
		return water;
	}

	public CityFeaturesEntity setWater(Integer water) {
		this.water = water;
		return this;
	}

	public Integer getGoodRoad() {
		return goodRoad;
	}

	public CityFeaturesEntity setGoodRoad(Integer goodRoad) {
		this.goodRoad = goodRoad;
		return this;
	}

	public Integer getSecurity() {
		return security;
	}

	public CityFeaturesEntity setSecurity(Integer security) {
		this.security = security;
		return this;
	}

	public Integer getSocialization() {
		return socialization;
	}

	public CityFeaturesEntity setSocialization(Integer socialization) {
		this.socialization = socialization;
		return this;
	}

	public Integer getPower() {
		return power;
	}

	public CityFeaturesEntity setPower(Integer power) {
		this.power = power;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public CityFeaturesEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
	
}
