package com.re.paas.api.infra.database.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchWriteItemRequest {

	private Map<String, List<WriteRequest>> requestItems = new HashMap<>();

	public Map<String, List<WriteRequest>> getRequestItems() {
		return requestItems;
	}

	public BatchWriteItemRequest setRequestItems(Map<String, List<WriteRequest>> requestItems) {
		this.requestItems = requestItems;
		return this;
	}
	
	public BatchWriteItemRequest addRequestItems(String tableName, List<WriteRequest> requestItems) {
		
		List<WriteRequest> requests = this.requestItems.get(tableName);
		
		if(requests == null) {
			this.requestItems.put(tableName, requestItems);
		} else {
			requests.addAll(requestItems);
		}
		return this;
	}
	
	public BatchWriteItemRequest addRequestItem(String tableName, WriteRequest requestItem) {
		return addRequestItems(tableName, Collections.unmodifiableList(Arrays.asList(requestItem)));
	}
	
}
