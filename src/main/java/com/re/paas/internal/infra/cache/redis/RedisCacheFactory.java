package com.re.paas.internal.infra.cache.redis;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.infra.cache.CacheAdapter;
import com.re.paas.api.infra.cache.CacheFactory;
import com.re.paas.api.infra.cache.DefaultCodec;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;

public class RedisCacheFactory implements CacheFactory<String, Object> {

	private static final List<String> bucketList = new ArrayList<>();

	private final RedisAdapter adapter;
	StatefulRedisConnection<String, Object> connection;

	RedisCacheFactory(RedisAdapter adapter, RedisConfig config) {
		this.adapter = adapter;
		this.connection = createRedisClient(config).connect(new RedisObjectCodec(new DefaultCodec()));
	}
	
	
	@Override
	public void initialize() {
		
	}

	@Override
	public CacheAdapter getAdapter() {
		return this.adapter;
	}

	@Override
	public RedisCache get(String bucket) {

		if (!bucketList.contains(bucket)) {
			bucketList.add(bucket);
		}

		RedisCache cache = new RedisCache(this.connection);
		return cache.setBucket(bucket);
	}

	private RedisClient createRedisClient(RedisConfig config) {

		StringBuilder credentials = new StringBuilder();

		if (config.getUsername() != null && config.getPassword() != null) {
			credentials.append(config.getUsername()).append(":").append(config.getPassword()).append("@");
		}

		StringBuilder uri = new StringBuilder().append("redis://").append(credentials).append(config.getHost())
				.append(":").append(config.getPort()).append("/").append(config.getDatabase()).append("?timeout=5s");

		
		ClientResources res = DefaultClientResources.builder()
                .build();
                
		RedisClient client = RedisClient.create(res, uri.toString());
		return client;
	}

	@Override
	public List<String> bucketList() {
		return bucketList;
	}

	@BlockerTodo
	@Override
	public void shutdown() {
		this.connection.flushCommands();
		this.connection.close();
	}
}
