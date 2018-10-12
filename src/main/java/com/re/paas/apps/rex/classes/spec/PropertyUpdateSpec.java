package com.re.paas.apps.rex.classes.spec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.re.paas.internal.classes.spec.YearlyPaymentPeriod;

public class PropertyUpdateSpec {

	Long id;
	
	Integer roomCount;

	Integer bathroomCount;

	Integer parkingSpaceCount;
	
	YearlyPaymentPeriod paymentPeriod;

	String currency;
	
	Double price;
	
	Boolean isFullyFurnished;

	List<String> images;

	String title;

	String videoTourLink;

	List<String> keywords;

	Map<Long, Boolean> properties;

	PropertyType type;

	Integer area;
	
	public PropertyUpdateSpec(Long id) {
		this.id = id;
		this.images = new ArrayList<String>();
		this.keywords = new ArrayList<>();
		this.properties = new HashMap<Long, Boolean>();
	}

	public Long getId() {
		return id;
	}

	public PropertyUpdateSpec setId(Long id) {
		this.id = id;
		return this;
	}
	
	public Integer getRoomCount() {
		return roomCount;
	}

	public PropertyUpdateSpec setRoomCount(Integer roomCount) {
		this.roomCount = roomCount;
		return this;
	}

	public Integer getBathroomCount() {
		return bathroomCount;
	}

	public PropertyUpdateSpec setBathroomCount(Integer bathroomCount) {
		this.bathroomCount = bathroomCount;
		return this;
	}
	
	public Integer getParkingSpaceCount() {
		return parkingSpaceCount;
	}

	public PropertyUpdateSpec setParkingSpaceCount(Integer parkingSpaceCount) {
		this.parkingSpaceCount = parkingSpaceCount;
		return this;
	}

	public YearlyPaymentPeriod getPaymentPeriod() {
		return paymentPeriod;
	}

	public PropertyUpdateSpec setPaymentPeriod(YearlyPaymentPeriod paymentPeriod) {
		this.paymentPeriod = paymentPeriod;
		return this;
	}

	public String getCurrency() {
		return currency;
	}

	public PropertyUpdateSpec setCurrency(String currency) {
		this.currency = currency;
		return this;
	}

	public Double getPrice() {
		return price;
	}

	public PropertyUpdateSpec setPrice(Double price) {
		this.price = price;
		return this;
	}

	public Boolean getIsFullyFurnished() {
		return isFullyFurnished;
	}

	public PropertyUpdateSpec setIsFullyFurnished(Boolean isFullyFurnished) {
		this.isFullyFurnished = isFullyFurnished;
		return this;
	}

	public List<String> getImages() {
		return images;
	}

	public PropertyUpdateSpec setImages(List<String> images) {
		this.images = images;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public PropertyUpdateSpec setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getVideoTourLink() {
		return videoTourLink;
	}

	public PropertyUpdateSpec setVideoTourLink(String videoTourLink) {
		this.videoTourLink = videoTourLink;
		return this;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public PropertyUpdateSpec setKeywords(List<String> keywords) {
		this.keywords = keywords;
		return this;
	}

	public Map<Long, Boolean> getProperties() {
		return properties;
	}

	public PropertyUpdateSpec setProperties(Map<Long, Boolean> properties) {
		this.properties = properties;
		return this;
	}

	public PropertyType getType() {
		return type;
	}

	public PropertyUpdateSpec setType(PropertyType type) {
		this.type = type;
		return this;
	}

	public Integer getArea() {
		return area;
	}

	public PropertyUpdateSpec setArea(Integer area) {
		this.area = area;
		return this;
	}
}
