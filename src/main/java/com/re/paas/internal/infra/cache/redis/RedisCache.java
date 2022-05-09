package com.re.paas.internal.infra.cache.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.infra.cache.AbstractCache;
import com.re.paas.api.infra.cache.Cache;
import com.re.paas.api.infra.cache.CacheEntryType;
import com.re.paas.api.infra.cache.CacheException;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;

public class RedisCache extends AbstractCache<String, Object> implements Cache<String, Object> {

	private String bucket;

	StatefulRedisConnection<String, Object> connection;
	RedisAsyncCommands<String, Object> commands;


	private Boolean strict;

	RedisCache(StatefulRedisConnection<String, Object> connection) {
		this.connection = connection;
		this.commands = this.connection.async();
		this.strict = false;
	}

	public RedisCache setBucket(String bucket) {
		this.bucket = bucket;
		return this;
	}

	private String key(String key) {
		return this.bucket + "_" + key;
	}

	@Override
	public CompletableFuture<Boolean> exists(String key) {
		return this.commands.exists(key(key)).thenApply(r -> {
			return r > 0;
		}).toCompletableFuture();
	}

	@Override
	public CompletableFuture<CacheEntryType> type(String key) {
		return this.commands.type(key(key)).thenApply(r -> {

			CacheEntryType type = null;

			switch (r) {
			case "string":
				type = CacheEntryType.PRIMITIVE;
			case "set":
				type = CacheEntryType.SET;
			case "hash":
				type = CacheEntryType.HASH;
			}
			return type;
		}).toCompletableFuture();
	}

	@Override
	public CompletableFuture<Object> get(String key) {
		return this.commands.get(key(key)).toCompletableFuture();
	}

	@Override
	public CompletableFuture<Object> set(String key, Object value) {
		RedisFuture<String> o = this.commands.set(key(key), value);
		return o.thenApply(r -> {

			if (!r.equals("OK")) {
				Exceptions.throwRuntime(
						new CacheException("Error occured during call to REDIS.set(..): " + o.getError()));
			}

			return value;
		}).toCompletableFuture();
	}
	
	@Override
	public CompletableFuture<Object> setex(String key, Object value, Long lifespan) {
		return this.set(key, value).thenCompose(r -> this.expireInSecs(key, lifespan).thenApply(b -> r));
	}

	@Override
	public CompletableFuture<Integer> del(String key) {
		return del(new String[] { key });
	}

	@Override
	public CompletableFuture<Integer> del(String... keys) {
		String[] keys_ = new String[keys.length];

		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			keys_[i] = key(key);
		}

		return this.commands.del(keys_).thenApply(r -> {
			return r.intValue();
		}).toCompletableFuture();
	}

	@Override
	public CompletableFuture<Boolean> expireInSecs(String key, Long seconds) {
		RedisFuture<Boolean> o = this.expire0(key(key), seconds);
		return o.thenApply(r -> {
					if (!r && this.strict) {
						Exceptions.throwRuntime(
								new CacheException("Error occured during call to REDIS.expire(): " + o.getError()));
					}
					return r;
				}).toCompletableFuture();
	}

	private RedisFuture<Boolean> expire0(String key, Long seconds) {
		return this.commands.expire(key, seconds);
	}

	@Override
	public CompletableFuture<Integer> incrby(String key, Integer amount) {
		return this.commands.incrby(key(key), amount).thenApply(r -> {
			return r.intValue();
		}).toCompletableFuture();
	}

	@Override
	public CompletableFuture<Integer> slength(String key) {
		return this.commands.scard(key(key)).thenApply(r -> {
			return r.intValue();
		}).toCompletableFuture();
	}

	@Override
	public CompletableFuture<Void> sget(String key, Function<Object, CompletableFuture<Void>> consumer) {

		return this.commands.smembers(key(key)).thenCompose(r -> {

			List<CompletableFuture<?>> futures = new ArrayList<>(r.size());

			r.forEach(e -> {
				futures.add(consumer.apply(e));
			});

			return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
			
		}).toCompletableFuture();
	}

	@Override
	public CompletableFuture<Integer> sadd(String key, List<Object> elements) {
		Object[] elementsArray = elements.toArray(new Object[elements.size()]);
		
		return this.commands.sadd(key(key), elementsArray).thenApply(r -> {
			return r.intValue();
		}).toCompletableFuture();
	}

	@Override
	public CompletableFuture<Integer> sdel(String key, Object value) {
		return sdel(key, new Object[] { value });
	}

	@Override
	public CompletableFuture<Integer> sdel(String key, Object... values) {
		return this.commands.srem(key(key), values)
		.thenApply(r -> {
			return r.intValue();
		})
		.toCompletableFuture();
	}

	@Override
	public CompletableFuture<Object> hset(String key, String field, Object value) {
		return this.hset0(key(key), field, value).thenApply(x -> value);
	}

	private CompletableFuture<Boolean> hset0(String key, String field, Object value) {
		return this.commands.hset(key, field, value).toCompletableFuture();
	}

	@Override
	public CompletableFuture<Object> hget(String key, String field) {
		return this.commands.hget(key(key), field).toCompletableFuture();
	}

	@Override
	public CompletableFuture<Map<String, Object>> hgetall(String key) {
		return this.commands.hgetall(key(key)).toCompletableFuture();
	}

	@Override
	public CompletableFuture<Integer> hdel(String key, String field) {
		return hdel(key, new String[] { field });
	}

	@Override public CompletableFuture<Integer> hdel(String key, String... fields) {
		return this.commands.hdel(key(key), fields).toCompletableFuture().thenApply(r -> r.intValue());
	}

	@Override
	public CompletableFuture<Void> hkeys(String key, Function<String, CompletableFuture<Void>> consumer) {

		return this.commands.hkeys(key(key)).thenCompose(r -> {

			List<CompletableFuture<?>> futures = new ArrayList<>(r.size());
			r.forEach(k -> {
				futures.add(consumer.apply(k));
			});

			return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		}).toCompletableFuture();
	}

	@Override
	public CompletableFuture<Integer> hincrby(String key, String field, Integer amount) {
		return this.commands.hincrby(key(key), field, amount)
				.thenApply(r -> r.intValue())
				.toCompletableFuture();
	}

	@Override
	public CompletableFuture<Integer> hlen(String key) {
		return this.commands.hlen(key(key)).thenApply(r -> {
			return r.intValue();
		}).toCompletableFuture();
	}

}
