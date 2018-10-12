package com.re.paas.apps.rex.classes.spec;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PropertyFloorPlanSpec {

	Long id;

	Integer floor;
	String description;

	Integer roomCount;
	Integer bathroomCount;
	Integer area;

	List<String> images;
	Date dateUpdated;

	
	public PropertyFloorPlanSpec() {
		this.images = new ArrayList<String>();
	}
	
	public Long getId() {
		return id;
	}

	public PropertyFloorPlanSpec setId(Long id) {
		this.id = id;
		return this;
	}

	public Integer getFloor() {
		return floor;
	}

	public PropertyFloorPlanSpec setFloor(Integer floor) {
		this.floor = floor;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public PropertyFloorPlanSpec setDescription(String description) {
		this.description = description;
		return this;
	}

	public Integer getRoomCount() {
		return roomCount;
	}

	public PropertyFloorPlanSpec setRoomCount(Integer roomCount) {
		this.roomCount = roomCount;
		return this;
	}

	public Integer getBathroomCount() {
		return bathroomCount;
	}

	public PropertyFloorPlanSpec setBathroomCount(Integer bathroomCount) {
		this.bathroomCount = bathroomCount;
		return this;
	}

	public Integer getArea() {
		return area;
	}

	public PropertyFloorPlanSpec setArea(Integer area) {
		this.area = area;
		return this;
	}

	public List<String> getImages() {
		return images;
	}

	public PropertyFloorPlanSpec setImages(List<String> images) {
		this.images = images;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public PropertyFloorPlanSpec setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

}
