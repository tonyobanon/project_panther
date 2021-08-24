package com.re.paas.api.infra.database.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchWriteItemSpec {

	private Map<String, List<WriteRequest>> requestItems = new HashMap<>();

	public Map<String, List<WriteRequest>> getRequestItems() {
		return requestItems;
	}
	
	public BatchWriteItemSpec setRequestItems(Map<String, List<WriteRequest>> requestItems) {
		this.requestItems = requestItems;
		return this;
	}
	
	public BatchWriteItemSpec addRequestItems(String tableName, List<WriteRequest> items) {
		addtItems0(tableName, items, this.requestItems);
		return this;
	}

	private void addtItems0(String tableName, List<WriteRequest> items, Map<String, List<WriteRequest>> dest) {

		List<WriteRequest> requests = dest.get(tableName);

		if (requests == null) {
			dest.put(tableName, items);
		} else {
			requests.addAll(items);
		}
	}
	
	public BatchWriteItemSpec addRequestItem(String tableName, WriteRequest requestItem) {
		return addRequestItems(tableName, Collections.unmodifiableList(Arrays.asList(requestItem)));
	}

}
