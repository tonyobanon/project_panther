package com.re.paas.api.infra.database.model;

import java.util.List;

import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.PrimaryKey;

public class ScanResult {

	private List<Item> items;

	/**
	 * <p>
	 * The number of items in the response.
	 * </p>
	 * <p>
	 * If you used a <code>QueryFilter</code> in the request, then
	 * <code>Count</code> is the number of items returned after the filter was
	 * applied, and <code>ScannedCount</code> is the number of matching items before
	 * the filter was applied.
	 * </p>
	 * <p>
	 * If you did not use a filter in the request, then <code>Count</code> and
	 * <code>ScannedCount</code> are the same.
	 * </p>
	 */
	private Integer count;
	/**
	 * <p>
	 * The number of items evaluated, before any <code>QueryFilter</code> is
	 * applied. A high <code>ScannedCount</code> value with few, or no,
	 * <code>Count</code> results indicates an inefficient <code>Query</code>
	 * operation.
	 * </p>
	 * <p>
	 * If you did not use a filter in the request, then <code>ScannedCount</code> is
	 * the same as <code>Count</code>.
	 * </p>
	 */
	private Integer scannedCount;
	/**
	 * <p>
	 * The primary key of the item where the operation stopped, inclusive of the
	 * previous result set. Use this value to start a new operation, excluding this
	 * value in the new request.
	 * </p>
	 * <p>
	 * If <code>LastEvaluatedKey</code> is empty, then the "last page" of results
	 * has been processed and there is no more data to be retrieved.
	 * </p>
	 * <p>
	 * If <code>LastEvaluatedKey</code> is not empty, it does not necessarily mean
	 * that there is more data in the result set. The only way to know when you have
	 * reached the end of the result set is when <code>LastEvaluatedKey</code> is
	 * empty.
	 * </p>
	 */
	private PrimaryKey lastEvaluatedKey;

	public List<Item> getItems() {
		return items;
	}

	public ScanResult setItems(List<Item> items) {
		this.items = items;
		return this;
	}

	public Integer getCount() {
		return count;
	}

	public ScanResult setCount(Integer count) {
		this.count = count;
		return this;
	}

	public Integer getScannedCount() {
		return scannedCount;
	}

	public ScanResult setScannedCount(Integer scannedCount) {
		this.scannedCount = scannedCount;
		return this;
	}

	public PrimaryKey getLastEvaluatedKey() {
		return lastEvaluatedKey;
	}

	public ScanResult setLastEvaluatedKey(PrimaryKey lastEvaluatedKey) {
		this.lastEvaluatedKey = lastEvaluatedKey;
		return this;
	}

}
