package com.re.paas.internal.classes.spec;

import java.util.Date;

public class BaseUserSpec {
	
	private Long id;
	private String role;
	
	private String name;
	private String image;
	private String description;
	
	private Date dateCreated;
	private Date dateUpdated;

	public Long getId() {
		return id;
	}

	public BaseUserSpec setId(Long id) {
		this.id = id;
		return this;
	}
	
	public String getRole() {
		return role;
	}

	public BaseUserSpec setRole(String role) {
		this.role = role;
		return this;
	}

	public String getName() {
		return name;
	}

	public BaseUserSpec setName(Object nameSpec) {
		this.name = nameSpec.toString();
		return this;
	}

	public String getImage() {
		return image;
	}

	public BaseUserSpec setImage(String image) {
		this.image = image;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public BaseUserSpec setDescription(String description) {
		this.description = description;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public BaseUserSpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public BaseUserSpec setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

}
