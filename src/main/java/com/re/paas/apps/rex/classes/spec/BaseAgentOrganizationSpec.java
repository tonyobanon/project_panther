package com.re.paas.apps.rex.classes.spec;

public class BaseAgentOrganizationSpec {

	Long id;
	String name;
	String email;
	String logo;

	Integer rating;

	String address;

	int city;
	String cityName;

	String territory;
	String territoryName;

	String country;
	String countryName;

	public BaseAgentOrganizationSpec() {
	}

	public Long getId() {
		return id;
	}

	public BaseAgentOrganizationSpec setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public BaseAgentOrganizationSpec setName(String name) {
		this.name = name;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public BaseAgentOrganizationSpec setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getLogo() {
		return logo;
	}

	public BaseAgentOrganizationSpec setLogo(String logo) {
		this.logo = logo;
		return this;
	}

	public Integer getRating() {
		return rating;
	}

	public BaseAgentOrganizationSpec setRating(Integer rating) {
		this.rating = rating;
		return this;
	}

	public String getAddress() {
		return address;
	}

	public BaseAgentOrganizationSpec setAddress(String address) {
		this.address = address;
		return this;
	}

	public int getCity() {
		return city;
	}

	public BaseAgentOrganizationSpec setCity(int city) {
		this.city = city;
		return this;
	}

	public String getCityName() {
		return cityName;
	}

	public BaseAgentOrganizationSpec setCityName(String cityName) {
		this.cityName = cityName;
		return this;
	}

	public String getTerritory() {
		return territory;
	}

	public BaseAgentOrganizationSpec setTerritory(String territory) {
		this.territory = territory;
		return this;
	}

	public String getTerritoryName() {
		return territoryName;
	}

	public BaseAgentOrganizationSpec setTerritoryName(String territoryName) {
		this.territoryName = territoryName;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public BaseAgentOrganizationSpec setCountry(String country) {
		this.country = country;
		return this;
	}

	public String getCountryName() {
		return countryName;
	}

	public BaseAgentOrganizationSpec setCountryName(String countryName) {
		this.countryName = countryName;
		return this;
	}

}
