package com.re.paas.internal.models;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.QueryKeys;
import com.re.paas.api.annotations.BlockerBlockerTodo;
import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.annotations.PlatformInternal;
import com.re.paas.api.annotations.Todo;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.classes.ResourceException;
import com.re.paas.api.listable.IndexedNameType;
import com.re.paas.api.listable.Listable;
import com.re.paas.api.listable.ListingFilter;
import com.re.paas.api.listable.ListingType;
import com.re.paas.api.listable.QueryFilter;
import com.re.paas.api.listable.SearchableUISpec;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.ModelMethod;
import com.re.paas.api.models.classes.IndexedNameSpec;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.caching.CacheAdapter;
import com.re.paas.internal.caching.CacheType;
import com.re.paas.internal.classes.CursorMoveType;
import com.re.paas.internal.classes.ListableContext;
import com.re.paas.internal.entites.IndexedNameEntity;
import com.re.paas.internal.fusion.functionalities.PlatformFunctionalities;
import com.re.paas.internal.fusion.functionalities.RoleFunctionalities;
import com.re.paas.internal.fusion.functionalities.SearchFunctionalities;
import com.re.paas.internal.models.errors.ListablesError;
import com.re.paas.internal.models.helpers.CacheHelper;
import com.re.paas.internal.models.helpers.EntityUtils;
import com.re.paas.internal.models.listables.IndexedNameTypes;

@BlockerTodo("Cleanup of listing contexts is poorly done, and this could be expensive")

@BlockerBlockerTodo("No sorting is done, please add support for sorting. It is extremely a Blocker")

@Todo("Make search functionality configurable")

public class SearchModel implements BaseModel {

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

	public static final String buildCacheListKey(IndexedNameType type, List<ListingFilter> listingFilters) {

		StringBuilder key = new StringBuilder().append(CACHE_KEY_LIST_$TYPE.replace("$TYPE", type.asString()));

		for (ListingFilter listingFilter : listingFilters) {

			key.append("____");

			for (Entry<String, Object> filter : listingFilter.getFilters().entrySet()) {
				key.append("__" + filter.getKey() + "_" + filter.getValue());
			}
		}

		return key.toString();
	}

	public static final String buildCacheSearchKey(IndexedNameType type, String phrase) {
		return CACHE_KEY_SEARCH_$TYPE_$PHRASE.replace("$TYPE", type.asString()).replace("$PHRASE", phrase);
	}

	/**
	 * Because cached list entries, can be updated in real-time by calling
	 * addCachedListKey(..) and removeCachedListKey(..), it always contains
	 * up-to-date data, and therefore has a cache type of PERSISTENT
	 */
	protected static final List<String> _list(IndexedNameType type, String order, List<ListingFilter> listingFilters) {

		String key = buildCacheListKey(type, listingFilters);

		List<String> cachedValue = CacheHelper.getListOrDefault(CacheType.PERSISTENT, key, () -> {
			CacheHelper.addToListOrCreate(CacheType.PERSISTENT, CACHE_KEY_LIST_ENTRIES, key);

			List<String> fetchedValue = list(type, order, listingFilters);
			return fetchedValue;
		});

		return cachedValue;
	}

	/**
	 * This has a cache type of SHORT_LIVED
	 */
	protected static final List<String> _search(IndexedNameType type, String phrase) {
		String key = buildCacheSearchKey(type, phrase);

		List<String> cachedValue = CacheHelper.getListOrDefault(CacheType.SHORT_LIVED, key, () -> {
			CacheHelper.addToListOrCreate(CacheType.PERSISTENT, CACHE_KEY_SEARCH_ENTRIES, key);

			List<String> fetchedValue = search(type, phrase);
			return fetchedValue;
		});

		return cachedValue;
	}

	public static void addCachedListKey(IndexedNameType type, Object elem) {
		addCachedListKey(type, new ArrayList<>(), elem);
	}

	public static void removeCachedListKey(IndexedNameType type, Object elem) {
		removeCachedListKey(type, new ArrayList<>(), elem);
	}

	/**
	 * Add a new key to a set of existing list keys for the specified type
	 */
	public static void addCachedListKey(IndexedNameType type, List<ListingFilter> listingFilters, Object elem) {
		String key = buildCacheListKey(type, listingFilters);
		CacheHelper.addToList(CacheType.PERSISTENT, key, elem.toString());
	}

	/**
	 * Remove a key to a set of existing list keys for the specified type
	 */
	public static void removeCachedListKey(IndexedNameType type, List<ListingFilter> listingFilters, Object elem) {
		String key = buildCacheListKey(type, listingFilters);
		CacheHelper.removeFromList(CacheType.PERSISTENT, key, elem.toString());
	}

	private static final String getCacheContextKey(String key) {
		return LISTABLE_CONTEXT_$KEY.replace("$KEY", key);
	}

	protected static String newContext(IndexedNameType type, List<String> keys, Integer pageSize) {

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

	@ModelMethod(functionality = SearchFunctionalities.Constants.PERFORM_LIST_OPERATION)
	public static String newListContext(Long userId, IndexedNameType type, Integer pageSize, String order,
			List<ListingFilter> listingFilters) {

		Listable<?> model = Listable.getDelegate().getListable(type);

		if (!model.canCreateContext()) {
			throw new PlatformException(ListablesError.CLIENT_CONTEXT_CREATION_NOT_ALLOWED, model.type().asString());
		}

		if (!model.authenticate(ListingType.LIST, userId, listingFilters)) {
			throw new ResourceException(ResourceException.ACCESS_NOT_ALLOWED);
		}

		List<String> keys = _list(type, order, listingFilters);

		return newContext(type, keys, pageSize);
	}

	/**
	 * Note: When a search result for a particular phrase is cached, it takes
	 * CacheType.SHORT_LIVED seconds to be clear from cache
	 */
	@ModelMethod(functionality = SearchFunctionalities.Constants.PERFORM_LIST_OPERATION)
	public static String newSearchContext(Long userId, IndexedNameType type, String phrase, Integer pageSize) {

		Listable<?> model = Listable.getDelegate().getListable(type);

		if (!model.canCreateContext()) {
			throw new PlatformException(ListablesError.CLIENT_CONTEXT_CREATION_NOT_ALLOWED, model.type().asString());
		}

		if (!model.authenticate(ListingType.SEARCH, userId, null)) {
			throw new ResourceException(ResourceException.ACCESS_NOT_ALLOWED);
		}

		List<String> keys = _search(type, phrase);
		return newContext(type, keys, pageSize);
	}

	@ModelMethod(functionality = PlatformFunctionalities.Constants.MANAGE_SYSTEM_CACHES)
	public static void clearCache(ListingType type) {
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

		if (!instance.authenticate(ListingType.LIST, userId, null)) {
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

		IndexedNameType type = ctx.getType();

		Listable<?> o = Listable.getDelegate().getListable(type);

		if (!o.authenticate(ListingType.LIST, userId, null)) {
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

	@ModelMethod(functionality = SearchFunctionalities.Constants.GET_SEARCHABLE_LISTS)
	public static Map<String, SearchableUISpec> getSearchableLists(Long userId) {

		Map<String, SearchableUISpec> result = new FluentHashMap<>();

		Listable.getDelegate().forEachSearchable(type -> {
			
			Listable<?> o = Listable.getDelegate().getListable(type);
			
			if (o.authenticate(ListingType.SEARCH, userId, null)) {
				result.put(type.asString(), Listable.getDelegate().getSearchable(type));
			}
			
		});

		return result;
	}

	/**
	 * This is equivalent to calling removeIndexedName(..), then addIndexedName(..)
	 * for two entries that contain the same name permutations. The advantage of
	 * using this function instead is that the name permutations do not need to be
	 * re-computed and stored
	 */
	public static void updateIndexedNameType(Object oldEntityId, Object newEntityId, IndexedNameType oldType,
			IndexedNameType newType) {

		ofy().load().type(IndexedNameEntity.class).filter("entityId = ", oldEntityId.toString()).forEach(e -> {
			if (e.getType().equals(oldType.asString())) {

				e.setEntityId(newEntityId.toString());
				e.setType(newType.asString());

				ofy().save().entity(e);
			}
		});
	}

	@Todo("Investigate if filter for an indexed field can be added to a query, and any filter for non-indexed field can be added afterwards")
	public static void removeIndexedName(String entityId, IndexedNameType type) {

		List<Key<?>> keys = new FluentArrayList<>();

		ofy().load().type(IndexedNameEntity.class).filter("entityId = ", entityId).forEach(e -> {
			if (e.getType().equals(type.asString())) {
				keys.add(Key.create(IndexedNameEntity.class, e.getId()));
			}
		});

		ofy().delete().keys(keys).now();
	}

	public static void addIndexedName(IndexedNameSpec spec, IndexedNameType type) {

		if (spec.getX() == null) {
			return;
		}

		// Run permutation function on names to get possible combinations

		List<String> nameList = new FluentArrayList<String>().with(spec.getX()).addIfNotNull(spec.getY())
				.addIfNotNull(spec.getZ());
		Integer[] indexes = Utils.indexes(nameList.size());

		List<IndexedNameEntity> ies = new FluentArrayList<>();

		Utils.permute(indexes).forEach(l1 -> {

			String x = null;
			String y = null;
			String z = null;

			switch (indexes.length) {
			case 1:
				x = nameList.get(l1.get(0));
				break;
			case 2:
				x = nameList.get(l1.get(0));
				y = nameList.get(l1.get(1));
				break;
			case 3:
				x = nameList.get(l1.get(0));
				y = nameList.get(l1.get(1));
				z = nameList.get(l1.get(2));
				break;
			}

			IndexedNameEntity ie = new IndexedNameEntity().setType(type.asString()).setEntityId(spec.getKey()).setX(x)
					.setY(y).setZ(z);

			ies.add(ie);
		});

		// Batch save

		ofy().save().entities(ies).now();
	}

	private static final <T> List<String> list(IndexedNameType type, String order, List<ListingFilter> listingFilters) {

		Listable<?> o = Listable.getDelegate().getListable(type);

		@SuppressWarnings("unchecked")
		Class<T> T = (Class<T>) o.entityType();

		// Add default filters
		listingFilters.addAll(o.defaultListingFilters());

		// Fetch all keys for this entity

		List<String> keys = new FluentArrayList<>();

		if (listingFilters.isEmpty()) {
			EntityUtils.lazyQuery(T, order).keys().forEach(k -> {
				keys.add(EntityUtils.toKeyString(k));
			});
		} else {

			for (ListingFilter listingFilter : listingFilters) {

				List<QueryFilter> filters = new FluentArrayList<>();

				listingFilter.getFilters().forEach((k, v) -> {
					filters.add(QueryFilter.get(k, v));
				});

				QueryKeys<T> queryKeys = EntityUtils
						.lazyQuery(T, order, filters.toArray(new QueryFilter[filters.size()])).keys();

				for (Key<T> k : queryKeys) {
					keys.add(EntityUtils.toKeyString(k));
				}
			}
		}

		return keys;
	}

	@BlockerTodo("Add metrics to measure performance")
	@PlatformInternal
	public static List<String> search(IndexedNameType type, String phrase) {

		if (Listable.getDelegate().getSearchable(type) == null) {
			throw new ResourceException(ResourceException.ACCESS_NOT_ALLOWED,
					"Index type: " + type.asString() + " is not searchable");
		}

		String[] fields = new String[] { "x", "y", "z" };
		String[] names = phrase.split("\\s");

		FluentHashMap<String, String> values = FluentHashMap.forNameMap();

		switch (names.length) {
		case 1:
			values.with(fields[0], names[0]);
			break;
		case 2:
			values.with(fields[0], names[0]).with(fields[1], names[1]);
			break;
		case 3:
			values.with(fields[0], names[0]).with(fields[1], names[1]).with(fields[2], names[2]);
			break;
		}

		List<Long> keys = new FluentArrayList<>();
		List<String> filteredKeys = new FluentArrayList<>();

		EntityUtils.search(IndexedNameEntity.class, values, o -> {
			keys.add(o.getId());
		});

		ofy().load().type(IndexedNameEntity.class).ids(keys).forEach((k, v) -> {
			if (v.getType().equals(type.getValue()) && !filteredKeys.contains(v.getEntityId())) {
				filteredKeys.add(v.getEntityId());
			}
		});

		return filteredKeys;
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
