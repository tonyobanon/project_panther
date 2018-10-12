package com.re.paas.internal.billing;

public class BillingAddress {

	private String street;
	private String houseNumberOrName;
	private String city;
	private String postalCode;
	private String stateOrProvince;
	private String country;

	public String getStreet() {
		return street;
	}

	public BillingAddress setStreet(String street) {
		this.street = street;
		return this;
	}

	public String getHouseNumberOrName() {
		return houseNumberOrName;
	}

	public BillingAddress setHouseNumberOrName(String houseNumberOrName) {
		this.houseNumberOrName = houseNumberOrName;
		return this;
	}

	public String getCity() {
		return city;
	}

	public BillingAddress setCity(String city) {
		this.city = city;
		return this;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public BillingAddress setPostalCode(String postalCode) {
		this.postalCode = postalCode;
		return this;
	}

	public String getStateOrProvince() {
		return stateOrProvince;
	}

	public BillingAddress setStateOrProvince(String stateOrProvince) {
		this.stateOrProvince = stateOrProvince;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public BillingAddress setCountry(String country) {
		this.country = country;
		return this;
	}

}
