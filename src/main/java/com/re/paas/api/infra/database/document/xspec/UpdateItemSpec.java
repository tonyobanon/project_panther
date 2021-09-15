package com.re.paas.api.infra.database.document.xspec;

import java.util.Collections;
import java.util.Map;

import com.re.paas.api.infra.database.document.PrimaryKey;

public class UpdateItemSpec extends BaseSpec {
	
	private PrimaryKey primaryKey;
    
    private final String updateExpression;
    private final String conditionExpression;

    private final Map<String, String> nameMap;
    private final Map<String, Object> valueMap;

    UpdateItemSpec(QueryBuilder builder) {
        SubstitutionContext context = new SubstitutionContext();
        this.updateExpression = builder.buildUpdateExpression(context);
        this.conditionExpression = builder.buildConditionExpression(context);
        final Map<String, String> nameMap = context.getNameMap();
        this.nameMap = nameMap == null ? null : Collections.unmodifiableMap(nameMap);
        Map<String, Object> valueMap = context.getValueMap();
        this.valueMap = valueMap == null ? null : Collections.unmodifiableMap(valueMap);
    }
    
    public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public UpdateItemSpec setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
		return this;
	}
    
    /**
     * Returns the update expression; or null if there is none.
     */
    public String getUpdateExpression() {
        return updateExpression;
    }

    /**
     * Returns the condition expression; or null if there is none.
     */
    public String getConditionExpression() {
        return conditionExpression;
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
}
