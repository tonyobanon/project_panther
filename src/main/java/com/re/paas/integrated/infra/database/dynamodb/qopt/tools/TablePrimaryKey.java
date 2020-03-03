package com.re.paas.integrated.infra.database.dynamodb.qopt.tools;

import java.util.Collection;
import java.util.Map.Entry;

import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.KeyAttribute;

public class TablePrimaryKey {

	private String hashKey;
	private Object hashValue;

	private String rangeKey;
	private Object rangeValue;

	public TablePrimaryKey(String tableName, Collection<KeyAttribute> keys) {

		String tableHashKey = AttributeModelImpl.tableToHashKey.get(tableName);
		Object tableHashValue = null;

		String tableRangeKey = null;
		Object tableRangeValue = null;

		for (KeyAttribute key : keys) {
			if (tableHashKey.equals(key.getName())) {
				tableHashValue = key.getValue();
			} else {
				tableRangeKey = key.getName();
				tableRangeValue = key.getValue();
			}
		}

		this.hashKey = tableHashKey;
		this.hashValue = tableHashValue;

		this.rangeKey = tableRangeKey;
		this.rangeValue = tableRangeValue;

	}

	public TablePrimaryKey(String tableName, Item item) {

		String tableHashKey = AttributeModelImpl.tableToHashKey.get(tableName);
		Object tableHashValue = null;

		String tableRangeKey = AttributeModelImpl.tableToRangeKey.get(tableName);
		Object tableRangeValue = null;

		for (Entry<String, Object> attr : item.attributes()) {

			if (tableHashKey.equals(attr.getKey())) {
				tableHashValue = attr.getValue();
			} else if (tableRangeKey != null && tableRangeKey.equals(attr.getKey())) {
				tableRangeValue = attr.getValue();
			}

			if(tableHashValue != null && (tableRangeKey == null || tableRangeValue != null)) {
				break;
			}
		}

		this.hashKey = tableHashKey;
		this.hashValue = tableHashValue;

		this.rangeKey = tableRangeKey;
		this.rangeValue = tableRangeValue;

	}

	public TablePrimaryKey(String tableName, KeyAttribute... keys) {

		String tableHashKey = AttributeModelImpl.tableToHashKey.get(tableName);
		Object tableHashValue = null;

		String tableRangeKey = null;
		Object tableRangeValue = null;

		for (KeyAttribute key : keys) {
			if (tableHashKey.equals(key.getName())) {
				tableHashValue = key.getValue();
			} else {
				tableRangeKey = key.getName();
				tableRangeValue = key.getValue();
			}
		}

		this.hashKey = tableHashKey;
		this.hashValue = tableHashValue;

		this.rangeKey = tableRangeKey;
		this.rangeValue = tableRangeValue;

	}

	public TablePrimaryKey(String hashKey, Object hashValue, String rangeKey, Object rangeValue) {

		this.hashKey = hashKey;
		this.hashValue = hashValue;

		this.rangeKey = rangeKey;
		this.rangeValue = rangeValue;

	}

	public TablePrimaryKey(String hashKey, Object hashValue) {

		this.hashKey = hashKey;
		this.hashValue = hashValue;

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
