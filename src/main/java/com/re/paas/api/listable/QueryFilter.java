package com.re.paas.api.listable;

public class QueryFilter {

	private String condition;
	private Object value;
	
	public QueryFilter() {
	}
	
	public QueryFilter(String condition, Object value) {
		this.condition = condition;
		this.value = value;
	}

	public String getCondition() {
		return condition;
	}

	public QueryFilter setCondition(String condition) {
		this.condition = condition;
		return this;
	}

	public Object getValue() {
		return value;
	}

	public QueryFilter setValue(Object value) {
		this.value = value;
		return this;
	}
	
	public static QueryFilter get(String condition, Object value) {
		return new QueryFilter(condition, value);
	}
	
	@Override
	public String toString() {
		return condition + value;
	}
}
