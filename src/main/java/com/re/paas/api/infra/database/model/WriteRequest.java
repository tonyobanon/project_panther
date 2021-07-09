package com.re.paas.api.infra.database.model;

import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.PrimaryKey;

public class WriteRequest {

	private final PrimaryKey deleteRequest;
	
	private final Item putRequest;
	
	public WriteRequest(PrimaryKey deleteRequest) {
		this.deleteRequest = deleteRequest;
		this.putRequest = null;
	}
	
	public WriteRequest(Item i) {
		this.deleteRequest = null;
		this.putRequest = i;
	}

	public PrimaryKey getDeleteRequest() {
		return deleteRequest;
	}

	public Item getPutRequest() {
		return putRequest;
	}
	
}
