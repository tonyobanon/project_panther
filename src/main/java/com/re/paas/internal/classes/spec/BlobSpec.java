package com.re.paas.internal.classes.spec;

import java.util.Date;

public class BlobSpec {

	private String id;
	private String mimeType;
	private byte[] data;
	private Integer size;
	private Date dateCreated;

	public String getMimeType() {
		return mimeType;
	}

	public BlobSpec setMimeType(String mimeType) {
		this.mimeType = mimeType;
		return this;
	}

	public byte[] getData() {
		return data;
	}

	public BlobSpec setData(byte[] data) {
		this.data = data;
		return this;
	}

	public String getId() {
		return id;
	}

	public BlobSpec setId(String id) {
		this.id = id;
		return this;
	}

	public Integer getSize() {
		return size;
	}

	public BlobSpec setSize(Integer size) {
		this.size = size;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public BlobSpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
}
