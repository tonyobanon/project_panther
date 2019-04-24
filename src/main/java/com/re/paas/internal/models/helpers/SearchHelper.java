package com.re.paas.internal.models.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.cmd.QueryKeys;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.listable.ListingFilter;
import com.re.paas.apps.rex.models.tables.PropertyTable;
import com.re.paas.internal.caching.CacheAdapter;
import com.re.paas.internal.caching.CacheType;
import com.re.paas.internal.models.SearchModel;
import com.re.paas.internal.models.listables.IndexedNameTypes;

public class SearchHelper {

	public static List<Long> getKeys(IndexedNameTypes type, ListingFilter filter, Callable<QueryKeys<PropertyTable>> fetch) {

		String key = SearchModel.buildCacheListKey(type, FluentArrayList.asList(filter));

		List<Long> result = (List<Long>) CacheAdapter.get(CacheType.PERSISTENT, key);

		if (result == null) {

			CacheHelper.addToListOrCreate(CacheType.PERSISTENT, SearchModel.CACHE_KEY_LIST_ENTRIES, key);

			result = new ArrayList<>();

			QueryResultIterator<Key<PropertyTable>> it = null;
			try {
				it = fetch.call().iterator();
			} catch (Exception e) {
				Exceptions.throwRuntime(e);
			}

			while (it.hasNext()) {
				result.add(it.next().getId());
			}

			CacheAdapter.put(CacheType.PERSISTENT, key, result);
		}

		return result;
	}
	
	
	public static List<Long> getKeys(IndexedNameTypes type, String phrase) {

		String key = SearchModel.buildCacheSearchKey(type, phrase);

		List<Long> result = (List<Long>) CacheAdapter.get(CacheType.SHORT_LIVED, key);

		if (result == null) {

			CacheHelper.addToListOrCreate(CacheType.PERSISTENT, SearchModel.CACHE_KEY_SEARCH_ENTRIES, key);

			result = new ArrayList<>();

			for(String s : SearchModel.search(type, phrase)){
				result.add(Long.parseLong(s));
			};

			CacheAdapter.put(CacheType.SHORT_LIVED, key, result);
		}

		return result;
	}
	
}
