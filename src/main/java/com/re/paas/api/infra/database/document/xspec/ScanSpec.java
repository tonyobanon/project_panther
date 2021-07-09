package com.re.paas.api.infra.database.document.xspec;

import java.util.Collections;
import java.util.Map;

import com.re.paas.api.infra.database.document.PrimaryKey;
import com.re.paas.api.infra.database.model.Select;

public final class ScanSpec extends BaseSpec {

	private final String projectionExpression;
	private final String filterExpression;

	private final Map<String, String> nameMap;
	private final Map<String, Object> valueMap;

	private PrimaryKey exclusiveStartKey;
	private Boolean consistentRead;
	
	private Select select;
	
	private Integer pageLimit;
	private Integer resultLimit;
	
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

	public Boolean isConsistentRead() {
		return consistentRead;
	}

	public ScanSpec setConsistentRead(Boolean consistentRead) {
		this.consistentRead = consistentRead;
		return this;
	}

	public Select getSelect() {
		return select;
	}

	public ScanSpec setSelect(Select select) {
		this.select = select;
		return this;
	}

	public Integer getPageLimit() {
		return pageLimit;
	}

	public ScanSpec setPageLimit(Integer pageLimit) {
		this.pageLimit = pageLimit;
		return this;
	}

	public Integer getResultLimit() {
		return resultLimit;
	}

	public ScanSpec setResultLimit(Integer resultLimit) {
		this.resultLimit = resultLimit;
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