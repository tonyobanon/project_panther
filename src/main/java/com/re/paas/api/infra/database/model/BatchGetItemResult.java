package com.re.paas.api.infra.database.model;

import java.util.List;
import java.util.Map;

import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.modelling.BaseTable;

public class BatchGetItemResult {

	/**
	 * <p>
	 * A map of table name to a list of items. Each object in <code>responses</code>
	 * consists of a table name, along with a list of all items that were returned
	 * </p>
	 */
	private Map<String, List<Item>> responses;

	public Map<String, List<Item>> getResponses() {
		return responses;
	}
	
	public List<Item> getResponses(Class<? extends BaseTable> table) {
		return getResponses().get(table.getSimpleName());
	}

	public BatchGetItemResult setResponses(Map<String, List<Item>> responses) {
		this.responses = responses;
		return this;
	}
}
