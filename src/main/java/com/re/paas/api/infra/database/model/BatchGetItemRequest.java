package com.re.paas.api.infra.database.model;

import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.infra.database.document.xspec.GetItemsSpec;

public class BatchGetItemRequest {

	private Map<String, GetItemsSpec> requestItems = new HashMap<>();

	public Map<String, GetItemsSpec> getRequestItems() {
		return requestItems;
	}

	public BatchGetItemRequest setRequestItems(Map<String, GetItemsSpec> requestItems) {
		this.requestItems = requestItems;
		return this;
	}

	public BatchGetItemRequest addRequestItem(String tableName, GetItemsSpec spec) {
		this.requestItems.put(tableName, spec);
		return this;
	}

}
