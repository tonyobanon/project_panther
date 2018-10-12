package com.re.paas.internal.entites.directory;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Cache
@Entity
public class AgentOrganizationEntity {

	@Id
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
	@Index
	String territory;
	String country;

	public AgentOrganizationEntity() {
		this.agents = new ArrayList<Long>();
	}
	
	public Long getId() {
		return id;
	}

	public AgentOrganizationEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public AgentOrganizationEntity setName(String name) {
		this.name = name;
		return this;
	}

	public Long getPhone() {
		return phone;
	}

	public AgentOrganizationEntity setPhone(Long phone) {
		this.phone = phone;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public AgentOrganizationEntity setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getLogo() {
		return logo;
	}

	public AgentOrganizationEntity setLogo(String logo) {
		this.logo = logo;
		return this;
	}
	
	public Long getAdmin() {
		return admin;
	}

	public AgentOrganizationEntity setAdmin(Long admin) {
		this.admin = admin;
		return this;
	}

	public List<Long> getAgents() {
		return agents;
	}

	public AgentOrganizationEntity setAgents(List<Long> agents) {
		this.agents = agents;
		return this;
	}

	public Integer getRating() {
		return rating;
	}

	public AgentOrganizationEntity setRating(Integer rating) {
		this.rating = rating;
		return this;
	}

	public String getAddress() {
		return address;
	}

	public AgentOrganizationEntity setAddress(String address) {
		this.address = address;
		return this;
	}

	public Integer getPostalCode() {
		return postalCode;
	}

	public AgentOrganizationEntity setPostalCode(Integer postalCode) {
		this.postalCode = postalCode;
		return this;
	}

	public Integer getCity() {
		return city;
	}

	public AgentOrganizationEntity setCity(Integer city) {
		this.city = city;
		return this;
	}

	public String getTerritory() {
		return territory;
	}

	public AgentOrganizationEntity setTerritory(String territory) {
		this.territory = territory;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public AgentOrganizationEntity setCountry(String country) {
		this.country = country;
		return this;
	}
	
}
