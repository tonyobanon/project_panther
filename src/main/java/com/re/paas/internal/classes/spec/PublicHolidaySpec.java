package com.re.paas.internal.classes.spec;

import java.util.Date;

public class PublicHolidaySpec {

	Long id;
	String name;
	String country;
	boolean isPublic;
	Date date;
	Date dateCreated;

	public Long getId() {
		return id;
	}

	public PublicHolidaySpec setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public PublicHolidaySpec setName(String name) {
		this.name = name;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public PublicHolidaySpec setCountry(String country) {
		this.country = country;
		return this;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public PublicHolidaySpec setPublic(boolean isPublic) {
		this.isPublic = isPublic;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public PublicHolidaySpec setDate(Date date) {
		this.date = date;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public PublicHolidaySpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

}
