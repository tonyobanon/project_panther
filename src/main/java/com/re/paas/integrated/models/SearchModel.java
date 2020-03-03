package com.re.paas.integrated.models;

import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.S;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.re.paas.api.annotations.develop.BlockerBlockerTodo;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.annotations.develop.PlatformInternal;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.classes.ResourceException;
import com.re.paas.api.events.BaseEvent;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Index;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.PrimaryKey;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.xspec.Condition;
import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;
import com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder;
import com.re.paas.api.infra.database.document.xspec.PutItemSpec;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.model.BatchWriteItemRequest;
import com.re.paas.api.infra.database.model.WriteRequest;
import com.re.paas.api.listable.ListableIndex;
import com.re.paas.api.listable.ListableIndexDeleteEvent;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.ModelMethod;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.utils.Utils;
import com.re.paas.integrated.fusion.functionalities.PlatformFunctionalities;
import com.re.paas.integrated.fusion.functionalities.SearchFunctionalities;
import com.re.paas.integrated.models.errors.ListablesError;
import com.re.paas.integrated.models.helpers.CacheHelper;
import com.re.paas.internal.caching.CacheAdapter;
import com.re.paas.internal.caching.CacheType;
import com.re.paas.internal.classes.CursorMoveType;
import com.re.paas.internal.classes.ListableContext;

@BlockerTodo("Cleanup of listing contexts is poorly done, and this could be expensive")

@BlockerBlockerTodo("No sorting is done, please add support for sorting. It is extremely a Blocker")

@Todo("Make search functionality configurable")

public class SearchModel extends BaseModel {

	// Persistent
	public static final String CACHE_KEY_LIST_$TYPE = "CACHE_KEY_LIST_$TYPE";
	// Short-lived
	public static final String CACHE_KEY_SEARCH_$TYPE_$PHRASE = "CACHE_KEY_SEARCH_$TYPE_$PHRASE";

	// Persistent
	public static final String CACHE_KEY_LIST_ENTRIES = "CACHE_KEY_LIST_ENTRIES";
	// Persistent
	public static final String CACHE_KEY_SEARCH_ENTRIES = "CACHE_KEY_SEARCH_ENTRIES";

	// Short-lived
	public static final String LISTABLE_CONTEXT_$KEY = "LISTABLE_CONTEXT_$KEY";

	@Override
	public void preInstall() {
		start();
	}

	@Override
	public String path() {
		return "core/search";
	}

	@Override
	public void start() {
		
		// Get notified when a listable is about to be deleted
		// This also the search index associated with the listable
		
		BaseEvent.one(ListableIndexDeleteEvent.class, e -> {
			
			// Are there context keys for this listable, if there are, prune
		});

	}

	private static List<String> getListKeys() {
		return CacheHelper.getListOrDefault(CacheType.PERSISTENT, CACHE_KEY_LIST_ENTRIES, () -> {
			return new FluentArrayList<>();
		});
	}

	private static List<String> getSearchKeys() {
		return CacheHelper.getListOrDefault(CacheType.PERSISTENT, CACHE_KEY_SEARCH_ENTRIES, () -> {
			return new FluentArrayList<>();
		});

	}

	public static final String buildCacheListKey(ListableIndex type, List<ListingFilter> listingFilters) {

		StringBuilder key = new StringBuilder().append(CACHE_KEY_LIST_$TYPE.replace("$TYPE", type.asString()));

		for (ListingFilter listingFilter : listingFilters) {

			key.append("____");

			for (Entry<String, Object> filter : listingFilter.getFilters().entrySet()) {
				key.append("__" + filter.getKey() + "_" + filter.getValue());
			}
		}

		return key.toString();
	}

	public static final String buildCacheSearchKey(ListableIndex type, String phrase) {
		return CACHE_KEY_SEARCH_$TYPE_$PHRASE.replace("$TYPE", type.asString()).replace("$PHRASE", phrase);
	}

	/**
	 * Because cached list entries, can be updated in real-time by calling
	 * addCachedListKey(..) and removeCachedListKey(..), it always contains
	 * up-to-date data, and therefore has a cache type of PERSISTENT
	 */
	protected static final List<String> _list(ListableIndex type, String order, List<ListingFilter> listingFilters) {

		String key = buildCacheListKey(type, listingFilters);

		List<String> cachedValue = CacheHelper.getListOrDefault(CacheType.PERSISTENT, key, () -> {
			CacheHelper.addToListOrCreate(CacheType.PERSISTENT, CACHE_KEY_LIST_ENTRIES, key);

			List<String> fetchedValue = list(type, order, listingFilters);
			return fetchedValue;
		});

		return cachedValue;
	}

	public static void addCachedListKey(ListableIndex type, Object elem) {
		addCachedListKey(type, new ArrayList<>(), elem);
	}

	public static void removeCachedListKey(ListableIndex type, Object elem) {
		removeCachedListKey(type, new ArrayList<>(), elem);
	}

	/**
	 * Add a new key to a set of existing list keys for the specified type
	 */
	public static void addCachedListKey(ListableIndex type, List<ListingFilter> listingFilters, Object elem) {
		String key = buildCacheListKey(type, listingFilters);
		CacheHelper.addToList(CacheType.PERSISTENT, key, elem.toString());
	}

	/**
	 * Remove a key to a set of existing list keys for the specified type
	 */
	public static void removeCachedListKey(ListableIndex type, List<ListingFilter> listingFilters, Object elem) {
		String key = buildCacheListKey(type, listingFilters);
		CacheHelper.removeFromList(CacheType.PERSISTENT, key, elem.toString());
	}

	private static final String getCacheContextKey(String key) {
		return LISTABLE_CONTEXT_$KEY.replace("$KEY", key);
	}

	protected static String newContext(ListableIndex type, List<String> keys, Integer pageSize) {

		Integer keysSize = keys.size();

		Integer pageCount = null;

		if (keysSize <= pageSize || pageSize == -1) {
			pageCount = keysSize > 0 ? 1 : 0;
			pageSize = keysSize;
		} else {
			pageCount = keysSize / pageSize;

			if (keysSize % pageSize > 0) {
				pageCount += 1;
			}
		}

		ListableContext ctx = new ListableContext().setType(type).setPageSize(pageSize).setCurrentPage(0)
				.setId(Utils.newRandom());

		int index = 0;

		for (int i = 1; i <= pageCount; i++) {

			List<String> pageKeys = new ArrayList<>();

			for (int j = 0; j < pageSize && index < keysSize; j++) {
				pageKeys.add(keys.get(index));
				index++;
			}

			ctx.addPage(i, pageKeys);
		}

		ctx.setPageCount(pageCount);

		CacheAdapter.put(CacheType.SHORT_LIVED, getCacheContextKey(ctx.getId()), ctx);

		return ctx.getId();
	}

	private static boolean _hasNext(ListableContext ctx) {
		return (ctx.getCurrentPage() < ctx.getPageCount());
	}

	private static boolean _hasPrevious(ListableContext ctx) {
		return (ctx.getCurrentPage() > 1);
	}

	private static ListableContext getContext(String contextKey) {
		String cacheKey = getCacheContextKey(contextKey);
		ListableContext ctx = (ListableContext) CacheAdapter.get(cacheKey);
		return ctx;
	}

	@ModelMethod(functionality = PlatformFunctionalities.Constants.MANAGE_SYSTEM_CACHES)
	public static void clearCache(ListingStrategy type) {
		switch (type) {
		case LIST:
			getListKeys().forEach(k -> {
				CacheAdapter.del(CacheType.PERSISTENT, k);
			});
			break;
		case SEARCH:
			getSearchKeys().forEach(k -> {
				CacheAdapter.del(CacheType.SHORT_LIVED, k);
			});
			break;
		}
	}

	@ModelMethod(functionality = SearchFunctionalities.Constants.PERFORM_LIST_OPERATION)
	public static Boolean has(Long userId, CursorMoveType moveType, String contextKey) {

		ListableContext ctx = getContext(contextKey);

		Listable<?> instance = Listable.getDelegate().getListable(ctx.getType());

		if (!instance.authenticate(ListingStrategy.LIST, userId, null)) {
			throw new ResourceException(ResourceException.ACCESS_NOT_ALLOWED);
		}

		switch (moveType) {
		case NEXT:
			return _hasNext(ctx);
		case PREVIOUS:
			return _hasPrevious(ctx);
		}
		return false;
	}

	@ModelMethod(functionality = SearchFunctionalities.Constants.PERFORM_LIST_OPERATION)
	public static Boolean isContextAvailable(String contextKey) {
		return CacheAdapter.containsKey(CacheType.SHORT_LIVED, getCacheContextKey(contextKey));
	}

	@ModelMethod(functionality = SearchFunctionalities.Constants.PERFORM_LIST_OPERATION)
	public static Map<?, ?> next(Long userId, CursorMoveType moveType, String contextKey) {

		ListableContext ctx = getContext(contextKey);

		ListableIndex type = ctx.getType();

		Listable<?> o = Listable.getDelegate().getListable(type);

		if (!o.authenticate(ListingStrategy.LIST, userId, null)) {
			throw new ResourceException(ResourceException.ACCESS_NOT_ALLOWED);
		}

		switch (moveType) {
		case NEXT:
			if (!_hasNext(ctx)) {
				return new HashMap<>();
			}
			break;
		case PREVIOUS:
			if (!_hasPrevious(ctx)) {
				return new HashMap<>();
			}
			break;
		}

		Integer currentPage = ctx.getCurrentPage() + (moveType.equals(CursorMoveType.NEXT) ? 1 : -1);

		List<String> keys = ctx.getPage(currentPage);

		ctx.setCurrentPage(currentPage);

		CacheAdapter.put(CacheType.SHORT_LIVED, getCacheContextKey(contextKey), ctx);

		return o.getAll(keys);
	}


	private static final <T> List<String> list(ListableIndex type, String order, List<ListingFilter> listingFilters) {

		Listable<?> o = Listable.getDelegate().getListable(type);

		@SuppressWarnings("unchecked")
		Class<T> T = (Class<T>) o.entityType();

		// Add default filters
		listingFilters.addAll(o.defaultListingFilters());

		// Fetch all keys for this entity

		List<String> keys = new FluentArrayList<>();

		if (listingFilters.isEmpty()) {

			// Add all keys, in order

		} else {

			// Add keys matching the filter, in order

		}

		return keys;
	}

	static {
	}

	@Override
	public void install(InstallOptions options) {

	}

	@Override
	public void update() {

	}

	@Override
	public void unInstall() {

	}

}
