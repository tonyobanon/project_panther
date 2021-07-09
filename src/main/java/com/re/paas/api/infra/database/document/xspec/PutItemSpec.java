package com.re.paas.api.infra.database.document.xspec;

import java.util.Collections;
import java.util.Map;

import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.model.ReturnValue;

public class PutItemSpec extends BaseSpec {
	
    private Item item;
	
    private ReturnValue returnValues;
	
    private final String conditionExpression;

    private final Map<String, String> nameMap;
    private final Map<String, Object> valueMap;

    PutItemSpec(ExpressionSpecBuilder builder) {
        SubstitutionContext context = new SubstitutionContext();
        this.conditionExpression = builder.buildConditionExpression(context);
        final Map<String, String> nameMap = context.getNameMap();
        this.nameMap = nameMap == null ? null : Collections.unmodifiableMap(nameMap);
        Map<String, Object> valueMap = context.getValueMap();
        this.valueMap = valueMap == null ? null : Collections.unmodifiableMap(valueMap);
    }
    
    public Item getItem() {
        return item;
    }
    
    public PutItemSpec withItem(Item item) {
        this.item = item;
        return this;
    }
    
    public ReturnValue getReturnValues() {
        return this.returnValues;
    }

    public PutItemSpec withReturnValues(ReturnValue returnValues) {
    	this.returnValues = returnValues;
        return this;
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
    
    public static PutItemSpec forItem(Item i) {
    	return new ExpressionSpecBuilder().buildForPut().withItem(i);
    }
}