package com.re.paas.apps.rex.classes.spec;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.re.paas.internal.classes.spec.YearlyPaymentPeriod;

public class PropertySpec {

	Long id;
	
	List<Long> listingStatusHistory;
	
	PropertyListingStatus listingStatus;
	
	List<ListedProperty> listings;
	
	Integer roomCount;

	Integer bathroomCount;

	Integer parkingSpaceCount;
	
	YearlyPaymentPeriod paymentPeriod;

	String currency;
	
	Double price;

	Double basePrice;
	
	Map<Long, Double> prices;
	
	Map<Long, Double> basePrices;
	
	List<Long> priceRules;
	
	Boolean isFullyFurnished;

	List<Long> floorPlan;
	
	List<String> images;

	String title;

	String videoTourLink;

	List<String> keywords;

	Map<Long, Boolean> properties;

	PropertyType type;

	Long agentOrganization;

	Integer area;

	String address;
	
	Integer zipCode;
	
	Integer city;

	String territory;

	String country;

	Date dateCreated;
	
	Long lastUpdatedBy;
	
	Date dateUpdated;

	
	public PropertySpec() {
		this.listingStatusHistory = new ArrayList<>();
		this.listings = new ArrayList<>();
		this.floorPlan = new ArrayList<>();
		this.images = new ArrayList<String>();
		this.keywords = new ArrayList<>();
		this.properties = new HashMap<Long, Boolean>();
	}

	public Long getId() {
		return id;
	}

	public PropertySpec setId(Long id) {
		this.id = id;
		return this;
	}

	public List<Long> getListingStatusHistory() {
		return listingStatusHistory;
	}

	public PropertySpec setListingStatusHistory(List<Long> listingStatusHistory) {
		this.listingStatusHistory = listingStatusHistory;
		return this;
	}

	public PropertyListingStatus getListingStatus() {
		return listingStatus;
	}

	public PropertySpec setListingStatus(PropertyListingStatus listingStatus) {
		this.listingStatus = listingStatus;
		return this;
	}

	public List<ListedProperty> getListings() {
		return listings;
	}

	public PropertySpec setListings(List<ListedProperty> listings) {
		this.listings = listings;
		return this;
	}

	public Integer getRoomCount() {
		return roomCount;
	}

	public PropertySpec setRoomCount(Integer roomCount) {
		this.roomCount = roomCount;
		return this;
	}

	public Integer getBathroomCount() {
		return bathroomCount;
	}

	public PropertySpec setBathroomCount(Integer bathroomCount) {
		this.bathroomCount = bathroomCount;
		return this;
	}
	
	public Integer getParkingSpaceCount() {
		return parkingSpaceCount;
	}

	public PropertySpec setParkingSpaceCount(Integer parkingSpaceCount) {
		this.parkingSpaceCount = parkingSpaceCount;
		return this;
	}

	public YearlyPaymentPeriod getPaymentPeriod() {
		return paymentPeriod;
	}

	public PropertySpec setPaymentPeriod(YearlyPaymentPeriod paymentPeriod) {
		this.paymentPeriod = paymentPeriod;
		return this;
	}

	public String getCurrency() {
		return currency;
	}

	public PropertySpec setCurrency(String currency) {
		this.currency = currency;
		return this;
	}
	
	public Double getPrice() {
		return price;
	}

	public PropertySpec setPrice(Double price) {
		this.price = price;
		return this;
	}

	public Double getBasePrice() {
		return basePrice;
	}

	public PropertySpec setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
		return this;
	}

	public Map<Long, Double> getPrices() {
		return prices;
	}

	public PropertySpec setPrices(Map<Long, Double> prices) {
		this.prices = prices;
		return this;
	}

	public Map<Long, Double> getBasePrices() {
		return basePrices;
	}

	public PropertySpec setBasePrices(Map<Long, Double> basePrices) {
		this.basePrices = basePrices;
		return this;
	}
	
	public List<Long> getPriceRules() {
		return priceRules;
	}

	public PropertySpec setPriceRules(List<Long> priceRules) {
		this.priceRules = priceRules;
		return this;
	}

	public Boolean getIsFullyFurnished() {
		return isFullyFurnished;
	}

	public PropertySpec setIsFullyFurnished(Boolean isFullyFurnished) {
		this.isFullyFurnished = isFullyFurnished;
		return this;
	}

	public List<Long> getFloorPlan() {
		return floorPlan;
	}

	public PropertySpec setFloorPlan(List<Long> floorPlan) {
		this.floorPlan = floorPlan;
		return this;
	}

	public List<String> getImages() {
		return images;
	}

	public PropertySpec setImages(List<String> images) {
		this.images = images;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public PropertySpec setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getVideoTourLink() {
		return videoTourLink;
	}

	public PropertySpec setVideoTourLink(String videoTourLink) {
		this.videoTourLink = videoTourLink;
		return this;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public PropertySpec setKeywords(List<String> keywords) {
		this.keywords = keywords;
		return this;
	}

	public Map<Long, Boolean> getProperties() {
		return properties;
	}

	public PropertySpec setProperties(Map<Long, Boolean> properties) {
		this.properties = properties;
		return this;
	}

	public PropertyType getType() {
		return type;
	}

	public PropertySpec setType(PropertyType type) {
		this.type = type;
		return this;
	}

	public Long getAgentOrganization() {
		return agentOrganization;
	}

	public PropertySpec setAgentOrganization(Long agentOrganization) {
		this.agentOrganization = agentOrganization;
		return this;
	}

	public Integer getArea() {
		return area;
	}

	public PropertySpec setArea(Integer area) {
		this.area = area;
		return this;
	}

	public String getAddress() {
		return address;
	}

	public PropertySpec setAddress(String address) {
		this.address = address;
		return this;
	}

	public Integer getZipCode() {
		return zipCode;
	}

	public PropertySpec setZipCode(Integer zipCode) {
		this.zipCode = zipCode;
		return this;
	}

	public Integer getCity() {
		return city;
	}

	public PropertySpec setCity(Integer city) {
		this.city = city;
		return this;
	}

	public String getTerritory() {
		return territory;
	}

	public PropertySpec setTerritory(String territory) {
		this.territory = territory;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public PropertySpec setCountry(String country) {
		this.country = country;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public PropertySpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Long getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public PropertySpec setLastUpdatedBy(Long lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public PropertySpec setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

}
