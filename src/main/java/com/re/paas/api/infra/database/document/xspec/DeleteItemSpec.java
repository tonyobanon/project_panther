package com.re.paas.api.infra.database.document.xspec;

import java.util.Collections;
import java.util.Map;

import com.re.paas.api.infra.database.document.PrimaryKey;

public final class DeleteItemSpec extends BaseSpec {

	private PrimaryKey primaryKey;

	private final String conditionExpression;

	private final Map<String, String> nameMap;
	private final Map<String, Object> valueMap;

	DeleteItemSpec(QueryBuilder builder) {
		SubstitutionContext context = new SubstitutionContext();
		this.conditionExpression = builder.buildConditionExpression(context);
		final Map<String, String> nameMap = context.getNameMap();
		this.nameMap = nameMap == null ? null : Collections.unmodifiableMap(nameMap);
		Map<String, Object> valueMap = context.getValueMap();
		this.valueMap = valueMap == null ? null : Collections.unmodifiableMap(valueMap);
	}

	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public DeleteItemSpec setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
		return this;
	}

	/**
	 * Returns the condition expression; or null if there is none.
	 */
	public final String getConditionExpression() {
		return conditionExpression;
	}

	/**
	 * Returns the name map which is unmodifiable; or null if there is none.
	 */
	@Override
	public final Map<String, String> getNameMap() {
		return nameMap;
	}

	/**
	 * Returns the value map which is unmodifiable; or null if there is none.
	 */
	public final Map<String, Object> getValueMap() {
		return valueMap;
	}

	public static DeleteItemSpec forKey(String hashKeyName, Object hashKeyValue) {
		return forKey(new PrimaryKey(hashKeyName, hashKeyValue));
	}
	
	public static DeleteItemSpec forKey(PrimaryKey key) {
		return new QueryBuilder().buildForDeleteItem()
				.setPrimaryKey(key);
	}
}
