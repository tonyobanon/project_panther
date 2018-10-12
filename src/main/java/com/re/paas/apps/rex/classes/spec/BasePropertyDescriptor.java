package com.re.paas.apps.rex.classes.spec;

import java.util.Date;

public class BasePropertyDescriptor {

	Long id;
	
	Double price;

	String currency;
	
	String title;
	
	PropertyType type;

	Long agentOrganization;

	Integer area;

	String address;

	Integer zipCode;
	
	Integer city;

	String territory;

	String country;

	Date dateCreated;

	Date dateUpdated;

	
	public Long getId() {
		return id;
	}

	public BasePropertyDescriptor setId(Long id) {
		this.id = id;
		return this;
	}

	public Double getPrice() {
		return price;
	}

	public BasePropertyDescriptor setPrice(Double price) {
		this.price = price;
		return this;
	}
	
	public String getCurrency() {
		return currency;
	}

	public BasePropertyDescriptor setCurrency(String currency) {
		this.currency = currency;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public BasePropertyDescriptor setTitle(String title) {
		this.title = title;
		return this;
	}

	public PropertyType getType() {
		return type;
	}

	public BasePropertyDescriptor setType(PropertyType type) {
		this.type = type;
		return this;
	}

	public Long getAgentOrganization() {
		return agentOrganization;
	}

	public BasePropertyDescriptor setAgentOrganization(Long agentOrganization) {
		this.agentOrganization = agentOrganization;
		return this;
	}

	public Integer getArea() {
		return area;
	}

	public BasePropertyDescriptor setArea(Integer area) {
		this.area = area;
		return this;
	}

	public String getAddress() {
		return address;
	}

	public BasePropertyDescriptor setAddress(String address) {
		this.address = address;
		return this;
	}

	public Integer getZipCode() {
		return zipCode;
	}

	public BasePropertyDescriptor setZipCode(Integer zipCode) {
		this.zipCode = zipCode;
		return this;
	}

	public Integer getCity() {
		return city;
	}

	public BasePropertyDescriptor setCity(Integer city) {
		this.city = city;
		return this;
	}

	public String getTerritory() {
		return territory;
	}

	public BasePropertyDescriptor setTerritory(String territory) {
		this.territory = territory;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public BasePropertyDescriptor setCountry(String country) {
		this.country = country;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public BasePropertyDescriptor setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public BasePropertyDescriptor setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

}
