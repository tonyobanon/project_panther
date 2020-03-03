package com.re.paas.api.infra.database.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;

public class BatchWriteItemRequest {

	private Map<String, List<WriteRequest>> requestItems = new HashMap<>();

	public Map<String, List<WriteRequest>> getRequestItems() {
		return requestItems;
	}

	public BatchWriteItemRequest setRequestItems(Map<String, List<WriteRequest>> requestItems) {
		this.requestItems = requestItems;
		return this;
	}

	public BatchWriteItemRequest addRequestItems(Class<?> table, List<WriteRequest> requestItems) {
		return addRequestItems(table.getSimpleName(), requestItems);
	}

	public BatchWriteItemRequest addRequestItems(String tableName, List<WriteRequest> requestItems) {

		List<WriteRequest> requests = this.requestItems.get(tableName);

		if (requests == null) {
			this.requestItems.put(tableName, requestItems);
		} else {
			requests.addAll(requestItems);
		}
		return this;
	}

	public BatchWriteItemRequest putAll(Class<?> table, List<Item> itemsToPut) {
		return this.addRequestItems(table,
				itemsToPut.stream().map(i -> new WriteRequest(i)).collect(Collectors.toList()));
	}

	public BatchWriteItemRequest deleteAll(Class<?> table, String hashKeyName, Object... hashKeyValuesToDelete) {
		return this.addRequestItems(table, Arrays.asList(hashKeyValuesToDelete).stream()
				.map(k -> new WriteRequest(DeleteItemSpec.forKey(hashKeyName, k))).collect(Collectors.toList()));
	}

	public BatchWriteItemRequest addRequestItem(Class<?> table, WriteRequest requestItem) {
		return addRequestItem(table.getSimpleName(), requestItem);
	}
	
	public BatchWriteItemRequest addRequestItem(String tableName, WriteRequest requestItem) {
		return addRequestItems(tableName, Collections.unmodifiableList(Arrays.asList(requestItem)));
	}

}
