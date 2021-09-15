package com.re.paas.api.infra.database.document.xspec;

import java.util.Collections;
import java.util.Map;

import com.re.paas.api.infra.database.document.PrimaryKey;

public final class GetItemSpec extends BaseGetItemSpec {

	private PrimaryKey primaryKey;

	GetItemSpec(QueryBuilder builder) {
		SubstitutionContext context = new SubstitutionContext();
		this.projectionExpression = builder.buildProjectionExpression(context);
		final Map<String, String> nameMap = context.getNameMap();
		this.nameMap = nameMap == null ? null : Collections.unmodifiableMap(nameMap);
	}

	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public GetItemSpec setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
		return this;
	}

	@Override
	public GetItemSpec setConsistentRead(Boolean consistentRead) {
		super.setConsistentRead(consistentRead);
		return this;
	}

	public static GetItemSpec forKey(PrimaryKey key, String... projections) {
		QueryBuilder expr = new QueryBuilder();
		for (String projection : projections) {
			expr.addProjection(projection);
		}
		return expr.buildForGetItem().setPrimaryKey(key);
	}

	public static GetItemSpec forKey(String hashKeyName, Object hashKeyValue, String... projections) {
		return forKey(new PrimaryKey(hashKeyName, hashKeyValue), projections);
	}
}
