package com.re.paas.internal.infra.cache.infinispan;

import java.util.ArrayList;
import java.util.List;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.multimap.api.embedded.MultimapCache;
import org.infinispan.multimap.api.embedded.MultimapCacheManager;

import com.re.paas.api.clustering.ClusteringServices;
import com.re.paas.api.infra.cache.Cache;
import com.re.paas.api.infra.cache.CacheAdapter;
import com.re.paas.api.infra.cache.CacheFactory;

public class InfinispanCacheFactory implements CacheFactory<String, Object> {

	private static final String basicCacheSuffix = "_$b";
	private static final String multimapCacheSuffix = "_$m";

	private static final List<String> bucketList = new ArrayList<>();

	private final InfinispanAdapter adapter;

	InfinispanCacheFactory(InfinispanAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public CacheAdapter getAdapter() {
		return this.adapter;
	}

	@Override
	public Cache<String, Object> get(String bucket) {

		if (!bucketList.contains(bucket)) {
			bucketList.add(bucket);
		}

		org.infinispan.Cache<String, Object> cache = ((DefaultCacheManager)ClusteringServices.get().getCacheManager())
				.getCache(bucket + basicCacheSuffix);

		@SuppressWarnings("unchecked")
		MultimapCache<String, Object> multimapCache = ((MultimapCacheManager<String, Object>)ClusteringServices.get().getMultimapCacheManager())
				.get(bucket + multimapCacheSuffix);

		return new InfinispanCache(cache, multimapCache);
	}

	@Override
	public List<String> bucketList() {
		return bucketList;
	}
}
