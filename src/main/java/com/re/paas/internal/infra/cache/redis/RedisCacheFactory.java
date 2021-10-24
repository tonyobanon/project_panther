package com.re.paas.internal.infra.cache.redis;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.infra.cache.Cache;
import com.re.paas.api.infra.cache.CacheAdapter;
import com.re.paas.api.runtime.spi.ShutdownPhase;

public class RedisCacheFactory extends RemoteCacheFactory {

	private static final List<String> bucketList = new ArrayList<>();

	private final RedisAdapter adapter;
	private final RedisConfig config;

	RedisCacheFactory(RedisAdapter adapter, String name, RedisConfig config) {
		super(name);
		this.adapter = adapter;
		this.config = config;
	}

	@Override
	public CacheAdapter getAdapter() {
		return this.adapter;
	}

	@Override
	protected RemoteCache newInstance() {
		return new RedisCache(this, this.config);
	}

	@Override
	public Cache<String, Object> get(String bucket) {

		if (!bucketList.contains(bucket)) {
			bucketList.add(bucket);
		}

		RedisCache cache = (RedisCache) get();
		return cache.setBucket(bucket);
	}

	protected Long getMaxConnections() {
		return config.getMaxConnections() - (int) (0.01 * config.getMaxConnections());
	}

	@Override
	public List<String> bucketList() {
		return bucketList;
	}

	@BlockerTodo
	@Override
	public void shutdown(ShutdownPhase phase) {

		RedisCache cache = (RedisCache) get();
		
		cache.commands.flushdb();

		// Close all client connections
		this.close();
		
		bucketList.clear();
	}
}
