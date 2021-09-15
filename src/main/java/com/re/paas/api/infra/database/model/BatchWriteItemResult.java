package com.re.paas.api.infra.database.model;

import java.util.List;
import java.util.Map;

public class BatchWriteItemResult {

	private final Map<String, List<WriteRequest>> unprocessedItems;

	public BatchWriteItemResult(Map<String, List<WriteRequest>> unprocessedItems) {
		this.unprocessedItems = unprocessedItems;
	}

	public Map<String, List<WriteRequest>> getUnprocessedItems() {
		return unprocessedItems;
	}

}
