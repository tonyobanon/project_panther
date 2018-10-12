package com.re.paas.internal.entites;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
@Cache
public class BlobEntity {

	@Id
	String id;
	byte[] data;
	String mimeType;
	Integer size;
	Date dateCreated;
	
	public String getId() {
		return id;
	}

	public BlobEntity setId(String id) {
		this.id = id;
		return this;
	}

	public byte[] getData() {
		return data;
	}

	public BlobEntity setData(byte[] data) {
		this.data = data;
		return this;
	}

	public String getMimeType() {
		return mimeType;
	}

	public BlobEntity setMimeType(String mimeType) {
		this.mimeType = mimeType;
		return this;
	}
	
	public Integer getSize() {
		return size;
	}

	public BlobEntity setSize(Integer size) {
		this.size = size;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public BlobEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

}
