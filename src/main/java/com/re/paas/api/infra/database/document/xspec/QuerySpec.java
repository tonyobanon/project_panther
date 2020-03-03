package com.re.paas.api.infra.database.document.xspec;

import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.*;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.re.paas.api.infra.database.document.PrimaryKey;
import com.re.paas.api.utils.ValueType;

public final class QuerySpec extends BaseSpec {

	private String indexName;

	private final String keyConditionExpression;

	private final String projectionExpression;
	private final String filterExpression;

	private final Map<String, String> nameMap;
	private final Map<String, Object> valueMap;

	private PrimaryKey exclusiveStartKey;
	private Boolean consistentRead;
	private Boolean scanIndexForward;

	private Integer limit;

	QuerySpec(ExpressionSpecBuilder builder) {

		SubstitutionContext context = new SubstitutionContext();
		this.keyConditionExpression = builder.buildKeyConditionExpression(context);
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

	public QuerySpec setIndexName(String indexName) {
		this.indexName = indexName;
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
	 * Returns the key condition expression; or null if there is none.
	 */
	public String getKeyConditionExpression() {
		return keyConditionExpression;
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

	public PrimaryKey getExclusiveStartKey() {
		return exclusiveStartKey;
	}

	public QuerySpec setExclusiveStartKey(PrimaryKey exclusiveStartKey) {
		this.exclusiveStartKey = exclusiveStartKey;
		return this;
	}

	public Boolean getConsistentRead() {
		return consistentRead;
	}

	public QuerySpec setConsistentRead(Boolean consistentRead) {
		this.consistentRead = consistentRead;
		return this;
	}

	public Boolean getScanIndexForward() {
		return scanIndexForward;
	}

	public QuerySpec setScanIndexForward(Boolean scanIndexForward) {
		this.scanIndexForward = scanIndexForward;
		return this;
	}

	public Integer getLimit() {
		return limit;
	}

	public QuerySpec setLimit(Integer limit) {
		this.limit = limit;
		return this;
	}

	public static QuerySpec get(String hashKey, String hashValues[], String... projections) {

		Condition hashCondition = null;

		for (String hashValue : hashValues) {

			Condition c = S(hashKey).eq(hashValue);

			if (hashCondition == null) {
				hashCondition = c;
			} else {
				hashCondition = hashCondition.or(c);
			}
		}

		ExpressionSpecBuilder expr = new ExpressionSpecBuilder().withKeyCondition(hashCondition);

		for (String projection : projections) {
			expr.addProjection(projection);
		}

		return expr.buildForQuery();
	}

	@SuppressWarnings("unchecked")
	public static QuerySpec get(String hashKey, String hashValue, String rangeKey, Object[] rangeValues,
			String... projections) {

		Condition condition = S(hashKey).eq(hashValue);

		if (rangeKey != null) {
			Condition rangeCondtion = null;
			for (Object rangeValue : rangeValues) {

				Condition c = rangeValue == null ? NULL(rangeKey).exists() : null;
				ValueType rangeValueType = ValueType.getType(rangeValue);
				
				switch(rangeValueType) {
				case BINARY:
					rangeCondtion = B(rangeKey).eq((ByteBuffer)rangeValue);
					break;
				case BINARY_SET:
					Set<ByteBuffer> binarySet = (Set<ByteBuffer>) rangeValue;
					rangeCondtion = BS(rangeKey).eq(binarySet.toArray(new ByteBuffer[binarySet.size()]));
					break;
				case BOOLEAN:
					rangeCondtion = BOOL(rangeKey).eq((Boolean) rangeValue);
					break;
				case DATE:
					rangeCondtion = D(rangeKey).eq((Date)rangeValue);
					break;
				case LIST:
					rangeCondtion = L(rangeKey).eq((List<?>)rangeValue);
					break;
				case MAP:
					rangeCondtion = M(rangeKey).eq((Map<String, ?>)rangeValue);
					break;
				case NUMBER:
					rangeCondtion = N(rangeKey).eq((Number)rangeValue);
					break;
				case NUMBER_SET:
					rangeCondtion = NS(rangeKey).eq((Set<Number>) rangeValue);
					break;
				case STRING_SET:
					Set<String> stringSet = (Set<String>) rangeValue;
					rangeCondtion = SS(rangeKey).eq(stringSet);
					break;
				case STRING:
					default:
					rangeCondtion = S(rangeKey).eq((String)rangeValue);
					break;
				}

				if (rangeCondtion == null) {
					rangeCondtion = c;
				} else {
					rangeCondtion = rangeCondtion.or(c);
				}
			}
			condition = condition.and(parenthesize(rangeCondtion));
		}

		ExpressionSpecBuilder expr = new ExpressionSpecBuilder().withKeyCondition(condition).addProjection(projections);

		return expr.buildForQuery();
	}
	
	public static QuerySpec get(String hashKey, String hashValue, String... projections) {
		return get(hashKey, hashValue, null, null, projections);
	}
}