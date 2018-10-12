package com.re.paas.apps.rex.classes.spec;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.internal.classes.spec.YearlyPaymentPeriod;

public class BasePropertySpec {

	Long id;
	
	ListedProperty listing;
	
	Integer roomCount;

	Integer bathroomCount;

	Integer parkingSpaceCount;
	
	YearlyPaymentPeriod paymentPeriod;

	Double price;
	
	Boolean isFullyFurnished;

	List<String> images;

	String title;
	
	String summary;
	
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

	
	public BasePropertySpec() {
		this.images = new ArrayList<String>();
	}

	public Long getId() {
		return id;
	}

	public BasePropertySpec setId(Long id) {
		this.id = id;
		return this;
	}

	public ListedProperty getListing() {
		return listing;
	}

	public BasePropertySpec setListing(ListedProperty listing) {
		this.listing = listing;
		return this;
	}

	public Integer getRoomCount() {
		return roomCount;
	}

	public BasePropertySpec setRoomCount(Integer roomCount) {
		this.roomCount = roomCount;
		return this;
	}

	public Integer getBathroomCount() {
		return bathroomCount;
	}

	public BasePropertySpec setBathroomCount(Integer bathroomCount) {
		this.bathroomCount = bathroomCount;
		return this;
	}
	
	public Integer getParkingSpaceCount() {
		return parkingSpaceCount;
	}

	public BasePropertySpec setParkingSpaceCount(Integer parkingSpaceCount) {
		this.parkingSpaceCount = parkingSpaceCount;
		return this;
	}

	public YearlyPaymentPeriod getPaymentPeriod() {
		return paymentPeriod;
	}

	public BasePropertySpec setPaymentPeriod(YearlyPaymentPeriod paymentPeriod) {
		this.paymentPeriod = paymentPeriod;
		return this;
	}

	public Double getPrice() {
		return price;
	}

	public BasePropertySpec setPrice(Double price) {
		this.price = price;
		return this;
	}

	public Boolean getIsFullyFurnished() {
		return isFullyFurnished;
	}

	public BasePropertySpec setIsFullyFurnished(Boolean isFullyFurnished) {
		this.isFullyFurnished = isFullyFurnished;
		return this;
	}

	public List<String> getImages() {
		return images;
	}

	public BasePropertySpec setImages(List<String> images) {
		this.images = images;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public BasePropertySpec setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getSummary() {
		return summary;
	}

	public BasePropertySpec setSummary(String summary) {
		this.summary = summary;
		return this;
	}

	public PropertyType getType() {
		return type;
	}

	public BasePropertySpec setType(PropertyType type) {
		this.type = type;
		return this;
	}

	public Long getAgentOrganization() {
		return agentOrganization;
	}

	public BasePropertySpec setAgentOrganization(Long agentOrganization) {
		this.agentOrganization = agentOrganization;
		return this;
	}

	public Integer getArea() {
		return area;
	}

	public BasePropertySpec setArea(Integer area) {
		this.area = area;
		return this;
	}

	public String getAddress() {
		return address;
	}

	public BasePropertySpec setAddress(String address) {
		this.address = address;
		return this;
	}

	public Integer getZipCode() {
		return zipCode;
	}

	public BasePropertySpec setZipCode(Integer zipCode) {
		this.zipCode = zipCode;
		return this;
	}

	public Integer getCity() {
		return city;
	}

	public BasePropertySpec setCity(Integer city) {
		this.city = city;
		return this;
	}

	public String getTerritory() {
		return territory;
	}

	public BasePropertySpec setTerritory(String territory) {
		this.territory = territory;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public BasePropertySpec setCountry(String country) {
		this.country = country;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public BasePropertySpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public BasePropertySpec setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

}
