package com.re.paas.apps.rex.classes.spec;

import com.re.paas.api.listable.ListingFilter;
import com.re.paas.internal.classes.spec.YearlyPaymentPeriod;

public class AdvancedPropertySearch extends PropertyListingRequest {

	private String searchTerm;
	private PropertyType propertyType;
	private Integer roomCount;
	private Integer bathroomCount;

	private Integer minArea;
	private Integer maxArea;

	private boolean forcePaymentPeriod;
	private YearlyPaymentPeriod paymentPeriod;
	private Double minPrice;
	private Double maxPrice;

	public String getSearchTerm() {
		return searchTerm;
	}

	public AdvancedPropertySearch setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
		return this;
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}

	public AdvancedPropertySearch setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
		return this;
	}

	public Integer getRoomCount() {
		return roomCount;
	}

	public AdvancedPropertySearch setRoomCount(Integer roomCount) {
		this.roomCount = roomCount;
		return this;
	}

	public Integer getBathroomCount() {
		return bathroomCount;
	}

	public AdvancedPropertySearch setBathroomCount(Integer bathroomCount) {
		this.bathroomCount = bathroomCount;
		return this;
	}

	public Integer getMinArea() {
		return minArea;
	}

	public AdvancedPropertySearch setMinArea(Integer minArea) {
		this.minArea = minArea;
		return this;
	}

	public Integer getMaxArea() {
		return maxArea;
	}

	public AdvancedPropertySearch setMaxArea(Integer maxArea) {
		this.maxArea = maxArea;
		return this;
	}

	public boolean isForcePaymentPeriod() {
		return forcePaymentPeriod;
	}

	public AdvancedPropertySearch setForcePaymentPeriod(boolean forcePaymentPeriod) {
		this.forcePaymentPeriod = forcePaymentPeriod;
		return this;
	}

	public YearlyPaymentPeriod getPaymentPeriod() {
		return paymentPeriod;
	}

	public AdvancedPropertySearch setPaymentPeriod(YearlyPaymentPeriod paymentPeriod) {
		this.paymentPeriod = paymentPeriod;
		return this;
	}

	public Double getMinPrice() {
		return minPrice;
	}

	public AdvancedPropertySearch setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
		return this;
	}

	public Double getMaxPrice() {
		return maxPrice;
	}

	public AdvancedPropertySearch setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
		return this;
	}

	@Override
	public ListingFilter getListingFilter() {

		ListingFilter filter = getDefaultListingFilter();

		if (searchTerm != null) {
			filter.addFilter("searchTerm", searchTerm.toLowerCase());
		}

		if (propertyType != null) {
			filter.addFilter("propertyType", propertyType.getValue());
		}

		if (roomCount != null) {
			filter.addFilter("roomCount", roomCount);
		}

		if (bathroomCount != null) {
			filter.addFilter("bathroomCount", bathroomCount);
		}

		if (minPrice != null) {
			filter.addFilter("minPrice", minPrice);
		}

		if (maxPrice != null) {
			filter.addFilter("maxPrice", maxPrice);
		}

		if (minArea != null) {
			filter.addFilter("minArea", minArea);
		}

		if (maxArea != null) {
			filter.addFilter("maxArea", maxArea);
		}

		return filter;
	}
}
