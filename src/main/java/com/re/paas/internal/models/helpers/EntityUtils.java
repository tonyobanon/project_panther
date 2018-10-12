package com.re.paas.internal.models.helpers;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.listable.QueryFilter;

public class EntityUtils {

	public static <T> void search(Class<T> type, Map<String, String> values, Consumer<Key<T>> consumer) {
		List<QueryFilter> filters = new FluentArrayList<>();

		values.forEach((field, prefix) -> {
			filters.add(QueryFilter.get(field + " >=", prefix));
			filters.add(QueryFilter.get(field + " <", prefix + "\uFFFD"));
		});

		EntityUtils.lazyQuery(type, filters.toArray(new QueryFilter[filters.size()])).keys().forEach(consumer);
	}

	public static <T> Query<T> lazyQuery(Class<T> type, QueryFilter... filters) {
		return lazyQuery(type, null, filters);
	}

	public static <T> Query<T> lazyQuery(Class<T> type, String order, QueryFilter... filters) {

		Query<T> query = ofy().load().type(type);

		for (int i = 0; i < filters.length; i++) {

			QueryFilter filter = filters[i];

			query = query.filter(filter.getCondition(), filter.getValue());
		}

		if (order != null) {
			query = query.order(order);
		}

		return query;
	}

	public static <T> List<T> query(Class<T> type, QueryFilter... filters) {

		List<T> entries = new FluentArrayList<>();

		Query<T> query = lazyQuery(type, filters);

		query.forEach(e -> {
			entries.add(e);
		});

		return entries;
	}

	public static String toKeyString(Key<?> k) {
		return k.getId() != 0 ? Long.valueOf(k.getId()).toString() : k.getName();
	}
}
