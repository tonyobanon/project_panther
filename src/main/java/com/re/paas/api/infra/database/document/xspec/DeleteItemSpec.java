package com.re.paas.api.infra.database.document.xspec;

import java.util.Collections;
import java.util.Map;

import com.re.paas.api.infra.database.document.PrimaryKey;
import com.re.paas.api.infra.database.model.ReturnValue;

public final class DeleteItemSpec extends BaseSpec {
	
	private PrimaryKey primaryKey;
    private String returnValues;
    
    private final String conditionExpression;

    private final Map<String, String> nameMap;
    private final Map<String, Object> valueMap;

    DeleteItemSpec(ExpressionSpecBuilder builder) {
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

	public String getReturnValues() {
        return this.returnValues;
    }

    public DeleteItemSpec withReturnValues(ReturnValue returnValues) {
    	this.returnValues = returnValues.toString();
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
}
