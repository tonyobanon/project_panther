package com.re.paas.api.infra.database.model;

import java.util.List;
import java.util.Map;

import com.re.paas.api.infra.database.document.Item;

public class BatchGetItemResult {

	/**
	 * <p>
	 * A map of table name to a list of items. Each object in <code>responses</code>
	 * consists of a table name, along with a list of all items that were returned
	 * </p>
	 */
	private Map<String, List<Item>> responses;
	
	private BatchGetItemRequest unprocessedKeys;

	public Map<String, List<Item>> getResponses() {
		return responses;
	}

	public BatchGetItemResult setResponses(Map<String, List<Item>> responses) {
		this.responses = responses;
		return this;
	}

	public BatchGetItemRequest getUnprocessedKeys() {
		return unprocessedKeys;
	}

	public BatchGetItemResult setUnprocessedKeys(BatchGetItemRequest unprocessedKeys) {
		this.unprocessedKeys = unprocessedKeys;
		return this;
	}

}