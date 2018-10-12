package com.re.paas.api.listable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.re.paas.api.classes.FluentHashMap;

public class ListingFilter {

	public static final Pattern queryFilterKey = Pattern
			.compile("((\\A[A-Za-z0-9]+\\p{Space}*)"
					+ "|"
					+ "(\\A[A-Za-z0-9]+?=\\p{Space}*(\\Q<\\E|\\Q>\\E|\\Q=\\E)*\\p{Space}*))");
	
	private Map<String, Object> filters;

	public ListingFilter() {
		filters = new HashMap<String, Object>();
	}

	public ListingFilter(String filterKey, Object filterValue) {
		this.filters = FluentHashMap.forValueMap().with(filterKey, filterValue);
	}

	public ListingFilter(Map<String, Object> filters) {
		this.filters = filters;
	}

	public Map<String, Object> getFilters() {
		return filters;
	}

	public ListingFilter setFilters(Map<String, Object> filters) {
		this.filters = filters;
		return this;
	}

	public ListingFilter addFilter(String filterKey, Object filterValue) {
		this.filters.put(filterKey, filterValue);
		return this;
	}

	public static ListingFilter fromQueryFilter(QueryFilter[] filters) {
		ListingFilter o = new ListingFilter();
		for (QueryFilter filter : filters) {
			o.addFilter(filter.getCondition(), filter.getValue());
		}
		return o;
	}
}
