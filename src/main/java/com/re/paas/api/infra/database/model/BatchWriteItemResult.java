package com.re.paas.api.infra.database.model;

public class BatchWriteItemResult {

	private final BatchWriteItemSpec unprocessedItems;

	public BatchWriteItemResult(BatchWriteItemSpec unprocessedItems) {
		this.unprocessedItems = unprocessedItems;
	}

	public BatchWriteItemSpec getUnprocessedItems() {
		return unprocessedItems;
	}

}
