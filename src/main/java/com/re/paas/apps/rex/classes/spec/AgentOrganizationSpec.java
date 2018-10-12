package com.re.paas.apps.rex.classes.spec;

import java.util.ArrayList;
import java.util.List;

public class AgentOrganizationSpec {

	Long id;
	String name;
	Long phone;
	String email;
	String logo;
	
	Long admin;
	List<Long> agents;
	
	Integer rating;
	
	String address;
	Integer postalCode;

	Integer city;
	String territory;
	String country;

	public AgentOrganizationSpec() {
		this.agents = new ArrayList<Long>();
	}
	
	public Long getId() {
		return id;
	}

	public AgentOrganizationSpec setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public AgentOrganizationSpec setName(String name) {
		this.name = name;
		return this;
	}

	public Long getPhone() {
		return phone;
	}

	public AgentOrganizationSpec setPhone(Long phone) {
		this.phone = phone;
		return this;
	}
	public String getEmail() {
		return email;
	}

	public AgentOrganizationSpec setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getLogo() {
		return logo;
	}

	public AgentOrganizationSpec setLogo(String logo) {
		this.logo = logo;
		return this;
	}
	
	public Long getAdmin() {
		return admin;
	}

	public AgentOrganizationSpec setAdmin(Long admin) {
		this.admin = admin;
		return this;
	}

	public List<Long> getAgents() {
		return agents;
	}

	public AgentOrganizationSpec setAgents(List<Long> agents) {
		this.agents = agents;
		return this;
	}

	public Integer getRating() {
		return rating;
	}

	public AgentOrganizationSpec setRating(Integer rating) {
		this.rating = rating;
		return this;
	}

	public String getAddress() {
		return address;
	}

	public AgentOrganizationSpec setAddress(String address) {
		this.address = address;
		return this;
	}

	public Integer getPostalCode() {
		return postalCode;
	}

	public AgentOrganizationSpec setPostalCode(Integer postalCode) {
		this.postalCode = postalCode;
		return this;
	}

	public Integer getCity() {
		return city;
	}

	public AgentOrganizationSpec setCity(Integer city) {
		this.city = city;
		return this;
	}

	public String getTerritory() {
		return territory;
	}

	public AgentOrganizationSpec setTerritory(String territory) {
		this.territory = territory;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public AgentOrganizationSpec setCountry(String country) {
		this.country = country;
		return this;
	}
	
}
