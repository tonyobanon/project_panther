package com.re.paas.internal.entites.locations;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Cache
public class TerritoryEntity {

	@Id
	String code;
	@Index
	String countryCode;

	String territoryName;

	public String getCode() {
		return code;
	}

	public TerritoryEntity setCode(String code) {
		this.code = code;
		return this;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public TerritoryEntity setCountryCode(String countryCode) {
		this.countryCode = countryCode;
		return this;
	}

	public String getTerritoryName() {
		return territoryName;
	}

	public TerritoryEntity setTerritoryName(String territoryName) {
		this.territoryName = territoryName;
		return this;
	}

}
