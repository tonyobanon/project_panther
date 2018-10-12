package com.re.paas.internal.entites.locations;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Cache
@Entity
public class PublicHolidayEntity {
 
	@Id
	Long id;
	String name;
	@Index
	String country;
	boolean isPublic;
	Date date;
	Date dateCreated;

	public Long getId() {
		return id;
	}

	public PublicHolidayEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public PublicHolidayEntity setName(String name) {
		this.name = name;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public PublicHolidayEntity setCountry(String country) {
		this.country = country;
		return this;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public PublicHolidayEntity setPublic(boolean isPublic) {
		this.isPublic = isPublic;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public PublicHolidayEntity setDate(Date date) {
		this.date = date;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public PublicHolidayEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

}
