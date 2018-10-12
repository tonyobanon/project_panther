package com.re.paas.internal.entites.directory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Stringify;
import com.re.paas.apps.rex.classes.spec.PropertyUpdateSpec;
import com.re.paas.gae_adapter.classes.LongStringifier;

@Cache
@Entity
public class PropertyEntity {

	@Id
	Long id;

	List<Long> listingStatusHistory;

	@Index
	Integer listingStatus;

	List<Long> listings;

	@Index
	Integer roomCount;

	@Index
	Integer bathroomCount;

	@Index
	Integer parkingSpaceCount;

	@Index
	Integer paymentPeriod;

	String currency;

	Double price;

	@Index
	Double basePrice;
	
	@Stringify(LongStringifier.class)
	Map<Long, Double> prices;

	@Stringify(LongStringifier.class)
	Map<Long, Double> basePrices;

	List<Long> priceRules;

	Boolean isFullyFurnished;

	List<Long> floorPlan;

	List<String> images;

	String title;

	String videoTourLink;

	List<String> keywords;

	@Stringify(LongStringifier.class)
	Map<Long, Boolean> properties;

	@Index
	Integer type;

	@Index
	String agentOrganization;

	@Index
	Integer area;

	@Index
	String address;

	@Index
	Integer zipCode;

	@Index
	Integer city;

	@Index
	String territory;

	String country;

	Date dateCreated;

	Long lastUpdatedBy;

	Date dateUpdated;

	public PropertyEntity() {
		this.listingStatusHistory = new ArrayList<>();
		this.listings = new ArrayList<Long>();
		this.prices  = new HashMap<>();
		this.basePrices = new HashMap<>();
		this.priceRules = new ArrayList<>();
		this.floorPlan = new ArrayList<>();
		this.images = new ArrayList<String>();
		this.keywords = new ArrayList<String>();
		this.properties = new HashMap<Long, Boolean>();
	}

	public Long getId() {
		return id;
	}

	public PropertyEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public List<Long> getListingStatusHistory() {
		return listingStatusHistory;
	}

	public PropertyEntity setListingStatusHistory(List<Long> listingStatusHistory) {
		this.listingStatusHistory = listingStatusHistory;
		return this;
	}

	public PropertyEntity addListingStatusHistory(Long listingStatusHistory) {
		this.listingStatusHistory.add(listingStatusHistory);
		return this;
	}

	public Integer getListingStatus() {
		return listingStatus;
	}

	public PropertyEntity setListingStatus(Integer listingStatus) {
		this.listingStatus = listingStatus;
		return this;
	}

	public List<Long> getListings() {
		return listings;
	}
	
	public PropertyEntity addListing(Long listing) {
		this.listings.add(listing);
		return this;
	}

	public PropertyEntity setListings(List<Long> listings) {
		this.listings = listings;
		return this;
	}

	public Integer getRoomCount() {
		return roomCount;
	}

	public PropertyEntity setRoomCount(Integer roomCount) {
		this.roomCount = roomCount;
		return this;
	}

	public Integer getBathroomCount() {
		return bathroomCount;
	}

	public PropertyEntity setBathroomCount(Integer bathroomCount) {
		this.bathroomCount = bathroomCount;
		return this;
	}

	public Integer getParkingSpaceCount() {
		return parkingSpaceCount;
	}

	public PropertyEntity setParkingSpaceCount(Integer parkingSpaceCount) {
		this.parkingSpaceCount = parkingSpaceCount;
		return this;
	}

	public Integer getPaymentPeriod() {
		return paymentPeriod;
	}

	public PropertyEntity setPaymentPeriod(Integer paymentPeriod) {
		this.paymentPeriod = paymentPeriod;
		return this;
	}

	public Double getPrice() {
		return price;
	}

	public PropertyEntity setPrice(Double price) {
		this.price = price;
		return this;
	}

	public String getCurrency() {
		return currency;
	}

	public PropertyEntity setCurrency(String currency) {
		this.currency = currency;
		return this;
	}

	public Double getBasePrice() {
		return basePrice;
	}

	public PropertyEntity setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
		return this;
	}
	
	public Map<Long, Double> getPrices() {
		return prices;
	}

	public PropertyEntity setPrices(Map<Long, Double> prices) {
		this.prices = prices;
		return this;
	}
	
	public PropertyEntity addPrice(Long ruleId, Double price) {
		this.prices.put(ruleId, price);
		return this;
	}
	
	public PropertyEntity removePrice(Long ruleId) {
		this.prices.remove(ruleId);
		return this;
	}

	public Map<Long, Double> getBasePrices() {
		return basePrices;
	}

	public PropertyEntity setBasePrices(Map<Long, Double> basePrices) {
		this.basePrices = basePrices;
		return this;
	}

	public PropertyEntity addBasePrice(Long ruleId, Double price) {
		this.basePrices.put(ruleId, price);
		return this;
	}
	
	public PropertyEntity removeBasePrice(Long ruleId) {
		this.basePrices.remove(ruleId);
		return this;
	}
	
	public List<Long> getPriceRules() {
		return priceRules;
	}

	public PropertyEntity addPriceRule(Long priceRule) {
		this.priceRules.add(priceRule);
		return this;
	}
	
	public PropertyEntity removePriceRule(Long priceRule) {
		this.priceRules.remove(priceRule);
		return this;
	}
	
	public PropertyEntity setPriceRules(List<Long> priceRules) {
		this.priceRules = priceRules;
		return this;
	}

	public Boolean getIsFullyFurnished() {
		return isFullyFurnished;
	}

	public PropertyEntity setIsFullyFurnished(Boolean isFullyFurnished) {
		this.isFullyFurnished = isFullyFurnished;
		return this;
	}

	public List<Long> getFloorPlan() {
		return floorPlan;
	}

	public PropertyEntity setFloorPlan(List<Long> floorPlan) {
		this.floorPlan = floorPlan;
		return this;
	}

	public PropertyEntity addFloorPlan(Long floorPlan) {
		this.floorPlan.add(floorPlan);
		return this;
	}

	public PropertyEntity removeFloorPlan(Long floorPlan) {
		this.floorPlan.remove(floorPlan);
		return this;
	}

	public List<String> getImages() {
		return images;
	}

	public PropertyEntity setImages(List<String> images) {
		this.images = images;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public PropertyEntity setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getVideoTourLink() {
		return videoTourLink;
	}

	public PropertyEntity setVideoTourLink(String videoTourLink) {
		this.videoTourLink = videoTourLink;
		return this;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public PropertyEntity setKeywords(List<String> keywords) {
		this.keywords = keywords;
		return this;
	}

	public Map<Long, Boolean> getProperties() {
		return properties;
	}

	public PropertyEntity setProperties(Map<Long, Boolean> properties) {
		this.properties = properties;
		return this;
	}

	public Integer getType() {
		return type;
	}

	public PropertyEntity setType(Integer type) {
		this.type = type;
		return this;
	}

	public Long getAgentOrganization() {
		return Long.parseLong(agentOrganization);
	}

	public PropertyEntity setAgentOrganization(Long agentOrganization) {
		this.agentOrganization = agentOrganization.toString();
		return this;
	}

	public Integer getArea() {
		return area;
	}

	public PropertyEntity setArea(Integer area) {
		this.area = area;
		return this;
	}

	public String getAddress() {
		return address;
	}

	public PropertyEntity setAddress(String address) {
		this.address = address;
		return this;
	}

	public Integer getZipCode() {
		return zipCode;
	}

	public PropertyEntity setZipCode(Integer zipCode) {
		this.zipCode = zipCode;
		return this;
	}

	public Integer getCity() {
		return city;
	}

	public PropertyEntity setCity(Integer city) {
		this.city = city;
		return this;
	}

	public String getTerritory() {
		return territory;
	}

	public PropertyEntity setTerritory(String territory) {
		this.territory = territory;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public PropertyEntity setCountry(String country) {
		this.country = country;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public PropertyEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Long getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public PropertyEntity setLastUpdatedBy(Long lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public PropertyEntity setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

	/** Utils */

	public static PropertyEntity getUpdate(PropertyEntity e, PropertyUpdateSpec spec) {

		if (spec.getIsFullyFurnished() != null) {
			e.setIsFullyFurnished(spec.getIsFullyFurnished());
		}
		if (spec.getArea() != null) {
			e.setArea(spec.getArea());
		}
		if (spec.getBathroomCount() != null) {
			e.setBathroomCount(spec.getBathroomCount());
		}
		if (spec.getImages() != null) {
			e.setImages(spec.getImages());
		}
		if (spec.getKeywords() != null) {
			e.setKeywords(spec.getKeywords());
		}
		if (spec.getParkingSpaceCount() != null) {
			e.setParkingSpaceCount(spec.getParkingSpaceCount());
		}
		if (spec.getPaymentPeriod() != null) {
			e.setPaymentPeriod(spec.getPaymentPeriod().getValue());
		}
		if (spec.getCurrency() != null) {
			e.setCurrency(spec.getCurrency());
		}
		if (spec.getPrice() != null) {
			e.setPrice(spec.getPrice());
		}
		if (spec.getProperties() != null) {
			e.setProperties(spec.getProperties());
		}
		if (spec.getRoomCount() != null) {
			e.setRoomCount(spec.getRoomCount());
		}
		if (spec.getTitle() != null) {
			e.setTitle(spec.getTitle());
		}
		if (spec.getType() != null) {
			e.setType(spec.getType().getValue());
		}
		if (spec.getVideoTourLink() != null) {
			e.setVideoTourLink(spec.getVideoTourLink());
		}
		return e;
	}
}
