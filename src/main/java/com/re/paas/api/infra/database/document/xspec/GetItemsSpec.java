package com.re.paas.api.infra.database.document.xspec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.re.paas.api.infra.database.document.PrimaryKey;

public final class GetItemsSpec extends BaseGetItemSpec {
	
	private List<PrimaryKey> primaryKeys = new ArrayList<>();

    GetItemsSpec(ExpressionSpecBuilder builder) {
        SubstitutionContext context = new SubstitutionContext();
        this.projectionExpression = builder.buildProjectionExpression(context);
        final Map<String, String> nameMap = context.getNameMap();
        this.nameMap = nameMap == null ? null : Collections.unmodifiableMap(nameMap);
    }

	public List<PrimaryKey> getPrimaryKeys() {
		return primaryKeys;
	}

	public GetItemsSpec setPrimaryKeys(List<PrimaryKey> primaryKeys) {
		this.primaryKeys = primaryKeys;
		return this;
	}
   
	public GetItemsSpec addPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKeys.add(primaryKey);
		return this;
	}
	
	@Override
	public GetItemsSpec setConsistentRead(Boolean consistentRead) {
		super.setConsistentRead(consistentRead);
		return this;
	}
	
	public static GetItemsSpec forKeys(Collection<PrimaryKey> keys, String... projections) {
		return forKeys(keys.toArray(new PrimaryKey[keys.size()]));
	}
	
	public static GetItemsSpec forKeys(PrimaryKey... keys) {
    	return new ExpressionSpecBuilder().buildForGetItems().setPrimaryKeys(Arrays.asList(keys));
    }
}
