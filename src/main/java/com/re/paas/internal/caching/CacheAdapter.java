package com.re.paas.internal.caching;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import com.google.appengine.api.memcache.stdimpl.GCacheFactory;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.models.Model;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.clustering.MasterNodeConfig;
import com.re.paas.internal.fusion.services.impl.JwtTokens;

@Model
//Used for caching User functionalities, Listing Context
public class CacheAdapter {

	private static final Long VOLATILE_CACHE_EXPIRY_DELTA = TimeUnit.SECONDS.toSeconds(30);
	private static final Long SHORTLIVED_CACHE_EXPIRY_DELTA = TimeUnit.HOURS.toSeconds(1);
	private static final Long LONGLIVED_CACHE_EXPIRY_DELTA = TimeUnit.DAYS.toSeconds(30);
	
	
	private static Cache volatileCache;
	private static Cache shortLivedCache;
	private static Cache LongLivedCache;
	private static Cache persistentCache;
	

	public static void start() {
		
		new MasterNodeConfig() {
			
		};

		Logger.get().info("Create application caches ..");
		
		volatileCache = newCache(new FluentHashMap<Integer, Object>().with(GCacheFactory.EXPIRATION_DELTA,
				VOLATILE_CACHE_EXPIRY_DELTA));

		shortLivedCache = newCache(new FluentHashMap<Integer, Object>().with(GCacheFactory.EXPIRATION_DELTA,
				SHORTLIVED_CACHE_EXPIRY_DELTA));

		LongLivedCache = newCache(new FluentHashMap<Integer, Object>().with(GCacheFactory.EXPIRATION_DELTA,
				LONGLIVED_CACHE_EXPIRY_DELTA));

		persistentCache = newCache(new FluentHashMap<Integer, Object>());

		CacheAdapter.put(JwtTokens.USER_SESSIONS_HASH, new FluentHashMap<>());
		CacheAdapter.put(JwtTokens.SESSION_ADDRESSES_HASH, new FluentHashMap<>());
	}

	private static final Cache newCache(Map<Integer, Object> properties) {
		
	    Cache cache = null;
		
		try {
			CacheFactory cacheFactory = CacheManager.get().getCacheFactory();
			cache = (cacheFactory.createCache(properties));
		} catch (CacheException e) {
			Exceptions.throwRuntime(e);
		}
		return cache;
	}

	public static Cache getVolatileCache() {
		return volatileCache;
	}
	
	public static Cache getShortLivedCache() {
		return shortLivedCache;
	}

	public static Cache getLonglivedCache() {
		return LongLivedCache;
	}

	public static Cache getPersistentCache() {
		return persistentCache;
	}

	private static final Cache getCache(CacheType type) {
		Cache cache = null;

		if(type == null) {
			type = CacheType.LONG_LIVED;
		}
		
		switch (type) {
		case LONG_LIVED:
			cache = getLonglivedCache();
			break;
		case PERSISTENT:
			cache = getPersistentCache();
			break;
		case SHORT_LIVED:
			cache = getShortLivedCache();
		case VOLATILE:
			cache = getVolatileCache();
			break;
		}
		return cache;
	}
	
	/**
	 * Unlike put(..), this method returns the new key mapped to the specified value
	 * */
	public static Object putTemp(Object value) {
		String key = Utils.newRandom();
		put(CacheType.VOLATILE, key, value);
		return key;
	}

	public static Object put(String key, Object value) {
		return put(null, key, value);
	}

	public static Object put(CacheType type, String key, Object value) {
		return getCache(type).put(key, value);
	}

	public static Object del(String key) {
		return del(null, key);
	}

	public static Object del(CacheType type, String key) {
		return getCache(type).remove(key);
	}

	public static Object get(CacheType type, String key) {
		return getCache(type).forEach(key);
	}

	public static Object get(String key) {
		return get(null, key);
	}
	
	public static Boolean containsKey(CacheType type, String key) {
		return getCache(type).containsKey(key);
	}
	
	public static List<String> getList(CacheType type, String key) {
		return (List<String>) getCache(type).forEach(key);
	}
	
	public static Map<String, Object> getMap(CacheType type, String key) {
		return (Map<String, Object>) getCache(type).forEach(key);
	}

	public static Integer getInt(String key) {
		Object o = get(key);
		if (o == null) {
			return null;
		}
		return Integer.parseInt(key);
	}
}
