package com.re.paas.api.infra.database.document.xspec;

import java.util.Map;

public class BaseGetItemSpec extends BaseSpec {

	protected Boolean consistentRead;
	
    protected String projectionExpression;
    protected Map<String, String> nameMap;
    

    /**
     * Returns the projection expression; or null if there is none.
     */
    public final String getProjectionExpression() {
        return projectionExpression;
    }

    /**
     * Returns the name map which is unmodifiable; or null if there is none.
     */
    @Override
    public final Map<String, String> getNameMap() {
        return nameMap;
    }

	public Boolean isConsistentRead() {
		return consistentRead;
	}

	public BaseGetItemSpec setConsistentRead(Boolean consistentRead) {
		this.consistentRead = consistentRead;
		return this;
	}

}
