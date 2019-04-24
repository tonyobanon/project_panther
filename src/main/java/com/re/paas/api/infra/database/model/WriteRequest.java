package com.re.paas.api.infra.database.model;

import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;
import com.re.paas.api.infra.database.document.xspec.PutItemSpec;

public class WriteRequest {

	private final DeleteItemSpec deleteRequest;
	
	private final PutItemSpec putRequest;
	
	public WriteRequest(DeleteItemSpec deleteRequest) {
		this.deleteRequest = deleteRequest;
		this.putRequest = null;
	}
	
	public WriteRequest(PutItemSpec putRequest) {
		this.deleteRequest = null;
		this.putRequest = putRequest;
	}

	public DeleteItemSpec getDeleteRequest() {
		return deleteRequest;
	}

	public PutItemSpec getPutRequest() {
		return putRequest;
	}
	
}
