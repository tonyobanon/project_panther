package com.re.paas.internal.entites.locations;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Cache
@Entity
public class CountryEntity {

	@Id
	String code;
	String countryName;

	String currencyCode;
	String currencyName;

	List<String> spokenLanguages;

	String languageCode;
	String dialingCode;

	
	public CountryEntity() {
		this.spokenLanguages = new ArrayList<>();
	}
	
	public String getCode() {
		return code;
	}

	public CountryEntity setCode(String code) {
		this.code = code;
		return this;
	}

	public String getCountryName() {
		return countryName;
	}

	public CountryEntity setCountryName(String countryName) {
		this.countryName = countryName;
		return this;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public CountryEntity setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
		return this;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public CountryEntity setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
		return this;
	}

	public List<String> getSpokenLanguages() {
		return spokenLanguages;
	}

	public CountryEntity setSpokenLanguages(List<String> spokenLanguages) {
		this.spokenLanguages = spokenLanguages;
		return this;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public CountryEntity setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
		return this;
	}

	public String getDialingCode() {
		return dialingCode;
	}

	public CountryEntity setDialingCode(String dialingCode) {
		this.dialingCode = dialingCode;
		return this;
	}
}
