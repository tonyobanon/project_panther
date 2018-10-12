package com.re.paas.internal.entites.locations;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Cache
public class CityEntity {

	@Id
	String id;
	@Index
	String territoryCode;

	String name;

	Double latitude;
	Double longitude;

	String timezone;

	public String getId() {
		return id;
	}

	public CityEntity setId(String id) {
		this.id = id;
		return this;
	}

	public String getTerritoryCode() {
		return territoryCode;
	}

	public CityEntity setTerritoryCode(String territoryCode) {
		this.territoryCode = territoryCode;
		return this;
	}

	public String getName() {
		return name;
	}

	public CityEntity setName(String name) {
		this.name = name;
		return this;
	}

	public Double getLatitude() {
		return latitude;
	}

	public CityEntity setLatitude(Double latitude) {
		this.latitude = latitude;
		return this;
	}

	public Double getLongitude() {
		return longitude;
	}

	public CityEntity setLongitude(Double longitude) {
		this.longitude = longitude;
		return this;
	}

	public String getTimezone() {
		return timezone;
	}

	public CityEntity setTimezone(String timezone) {
		this.timezone = timezone;
		return this;
	}

}
