package com.re.paas.api.infra.database.model;

public class BatchWriteItemResult {

	private final BatchWriteItemRequest unprocessedItems;

	public BatchWriteItemResult(BatchWriteItemRequest unprocessedItems) {
		this.unprocessedItems = unprocessedItems;
	}

	public BatchWriteItemRequest getUnprocessedItems() {
		return unprocessedItems;
	}

}
