package com.re.paas.api.infra.database.document.xspec;

import java.util.Collections;
import java.util.Map;

import com.re.paas.api.infra.database.document.PrimaryKey;

public final class ScanSpec extends BaseSpec {

	private String indexName;

	private final String projectionExpression;
	private final String filterExpression;

	private final Map<String, String> nameMap;
	private final Map<String, Object> valueMap;

	private PrimaryKey exclusiveStartKey;
	private Boolean consistentRead;
	private Integer limit;
	private Integer totalSegments;
	private Integer segment;

	ScanSpec(ExpressionSpecBuilder builder) {
		SubstitutionContext context = new SubstitutionContext();
		this.filterExpression = builder.buildConditionExpression(context);
		this.projectionExpression = builder.buildProjectionExpression(context);

		final Map<String, String> nameMap = context.getNameMap();
		this.nameMap = nameMap == null ? null : Collections.unmodifiableMap(nameMap);
		Map<String, Object> valueMap = context.getValueMap();
		this.valueMap = valueMap == null ? null : Collections.unmodifiableMap(valueMap);
	}

	public String getIndexName() {
		return indexName;
	}

	public ScanSpec setIndexName(String indexName) {
		this.indexName = indexName;
		return this;
	}

	public PrimaryKey getExclusiveStartKey() {
		return exclusiveStartKey;
	}

	public ScanSpec setExclusiveStartKey(PrimaryKey exclusiveStartKey) {
		this.exclusiveStartKey = exclusiveStartKey;
		return this;
	}

	/**
	 * Returns the projection expression; or null if there is none.
	 */
	public String getProjectionExpression() {
		return projectionExpression;
	}

	/**
	 * Returns the condition expression; or null if there is none.
	 */
	public String getFilterExpression() {
		return filterExpression;
	}

	/**
	 * Returns the name map which is unmodifiable; or null if there is none.
	 */
	@Override
	public Map<String, String> getNameMap() {
		return nameMap;
	}

	/**
	 * Returns the value map which is unmodifiable; or null if there is none.
	 */
	public Map<String, Object> getValueMap() {
		return valueMap;
	}

	public Boolean getConsistentRead() {
		return consistentRead;
	}

	public ScanSpec setConsistentRead(Boolean consistentRead) {
		this.consistentRead = consistentRead;
		return this;
	}

	public Integer getLimit() {
		return limit;
	}

	public ScanSpec setLimit(Integer limit) {
		this.limit = limit;
		return this;
	}

	public Integer getTotalSegments() {
		return totalSegments;
	}

	public ScanSpec setTotalSegments(Integer totalSegments) {
		this.totalSegments = totalSegments;
		return this;
	}

	public Integer getSegment() {
		return segment;
	}

	public ScanSpec setSegment(Integer segment) {
		this.segment = segment;
		return this;
	}
}