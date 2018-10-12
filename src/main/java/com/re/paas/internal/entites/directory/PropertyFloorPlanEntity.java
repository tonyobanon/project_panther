package com.re.paas.internal.entites.directory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Cache
@Entity
public class PropertyFloorPlanEntity {

	@Id
	Long id;

	Integer floor;
	String description;

	Integer roomCount;
	Integer bathroomCount;
	Integer area;

	List<String> images;
	Date dateUpdated;

	public PropertyFloorPlanEntity() {
		this.images = new ArrayList<String>();
	}

	public Long getId() {
		return id;
	}

	public PropertyFloorPlanEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public Integer getFloor() {
		return floor;
	}

	public PropertyFloorPlanEntity setFloor(Integer floor) {
		this.floor = floor;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public PropertyFloorPlanEntity setDescription(String description) {
		this.description = description;
		return this;
	}

	public Integer getRoomCount() {
		return roomCount;
	}

	public PropertyFloorPlanEntity setRoomCount(Integer roomCount) {
		this.roomCount = roomCount;
		return this;
	}

	public Integer getBathroomCount() {
		return bathroomCount;
	}

	public PropertyFloorPlanEntity setBathroomCount(Integer bathroomCount) {
		this.bathroomCount = bathroomCount;
		return this;
	}

	public Integer getArea() {
		return area;
	}

	public PropertyFloorPlanEntity setArea(Integer area) {
		this.area = area;
		return this;
	}

	public List<String> getImages() {
		return images;
	}

	public PropertyFloorPlanEntity setImages(List<String> images) {
		this.images = images;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public PropertyFloorPlanEntity setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

}
