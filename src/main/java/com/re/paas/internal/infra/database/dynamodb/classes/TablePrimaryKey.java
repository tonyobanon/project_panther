package com.re.paas.internal.infra.database.dynamodb.classes;

import java.util.Collection;

import com.re.paas.api.infra.database.document.KeyAttribute;
import com.re.paas.api.utils.Collections;

public class TablePrimaryKey {

	private String tableName;

	private String hashKey;
	private Object hashValue;

	private String rangeKey;
	private Object rangeValue;
	

	public TablePrimaryKey(String tableName, Collection<KeyAttribute> keys) {
		
		KeyAttribute hash = Collections.nthValue(keys, 0);
		KeyAttribute range = Collections.nthValue(keys, 1);
		
		this.hashKey = hash.getName();
		this.hashValue = hash.getValue();

		if (range != null) {
			this.rangeKey = range.getName();
			this.rangeValue = range.getValue();
		}
	}
	
	public String getTableName() {
		return tableName;
	}

	public String getHashKey() {
		return hashKey;
	}

	public void setHashKey(String hashKey) {
		this.hashKey = hashKey;
	}

	public Object getHashValue() {
		return hashValue;
	}

	public void setHashValue(Object hashValue) {
		this.hashValue = hashValue;
	}

	public String getRangeKey() {
		return rangeKey;
	}

	public void setRangeKey(String rangeKey) {
		this.rangeKey = rangeKey;
	}

	public Object getRangeValue() {
		return rangeValue;
	}

	public void setRangeValue(Object rangeValue) {
		this.rangeValue = rangeValue;
	}

}
