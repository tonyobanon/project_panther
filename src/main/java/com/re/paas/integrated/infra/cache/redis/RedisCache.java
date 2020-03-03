package com.re.paas.integrated.infra.cache.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.infra.cache.CacheEntryType;
import com.re.paas.api.infra.cache.CacheException;
import com.re.paas.api.utils.Utils;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;

public class RedisCache extends RemoteCache {

	/**
	 * Unlike {@link #keyInvalidationStatHash}, this variable which is private to
	 * {@link RedisCache} is used to keep track of entries for which an inactivity
	 * timeout has been set, each time an activity is done on such key(s),
	 * {@link #expire(String, Long)} is called
	 */
	// k: "abc", v: "15000"
	private static final String keyInvalidationStatHash = "__$kis";

	private String instanceId;
	private final RedisConfig config;

	private String bucket;

	StatefulRedisConnection<String, Object> connection;
	RedisAsyncCommands<String, Object> commands;

	private static final Integer defaultLimit = 1000;
	// private static final Integer maxLimit = 100000;

	private Integer limit;
	private Boolean strict;

	RedisCache(RedisCacheFactory factory, RedisConfig config) {

		this(Utils.newShortRandom(), factory, config);

		this.limit = defaultLimit;
		this.strict = false;
	}

	RedisCache(String instanceId, RemoteCacheFactory factory, RedisConfig config) {

		super(factory);

		this.instanceId = instanceId;
		this.config = config;

		this.connection = createRedisClient().connect(new RedisObjectCodec(getCodec()));
		this.commands = this.connection.async();
	}

	public String getInstanceId() {
		return instanceId;
	}

	public RedisCache setBucket(String bucket) {
		this.bucket = bucket;
		return this;
	}

	private String key(String key) {

		if (Utils.equals(key, keyInvalidationStatHash)) {
			Exceptions.throwRuntime(new IllegalArgumentException("Key: '" + key + "' is not allowed."));
		}

		return this.bucket + "_" + key;
	}

	/**
	 * Each time an activity happens on a key, and the key is set to be evicted by
	 * inactivity, then we need to refresh the expiry
	 * 
	 * @param key
	 * @return
	 */
	private CompletableFuture<Boolean> updateKeyExpiry(String key) {

		return hget0(keyInvalidationStatHash, key).thenCompose(r -> {

			if (r == null) {
				return null;
			}

			return expire0(key, (Long) r);
		});
	}

	/**
	 * This removes the specified key(s) from keyInvalidationStatHash, if necessary
	 * 
	 * @param keys
	 * @return
	 */
	private CompletableFuture<Boolean> setKeyExpiry(String key, Long duration) {
		return this.hset0(keyInvalidationStatHash, key, duration);
	}

	/**
	 * This removes the specified key(s) from keyInvalidationStatHash, if necessary
	 * 
	 * @param keys
	 * @return
	 */
	private CompletableFuture<Integer> removeKeyExpiry(String... keys) {

		CompletableFuture<?>[] futures = new CompletableFuture[keys.length];

		for (int i = 0; i < keys.length; i++) {
			futures[i] = this.hdel0(keyInvalidationStatHash, keys[i]);
		}
		return CompletableFuture.allOf(futures).thenApply(r -> keys.length);
	}

	private RedisClient createRedisClient() {

		StringBuilder credentials = new StringBuilder();
		RedisConfig config = this.config;

		if (config.getUsername() != null && config.getPassword() != null) {
			credentials.append(config.getUsername()).append(":").append(config.getPassword()).append("@");
		}

		StringBuilder uri = new StringBuilder().append("redis://").append(credentials).append(config.getHost())
				.append(":").append(config.getPort()).append("/").append(config.getDatabase());

		return RedisClient.create(uri.toString());
	}

	@Override
	public CompletableFuture<Boolean> exists(String key) {
		this.startOperation();
		return this.commands.exists(key(key)).toCompletableFuture().thenCompose(r -> {
			return updateKeyExpiry(key(key)).thenApply(s -> r);
		}).thenApply(r -> {
			this.finishOperation();
			return r > 0;
		});
	}

	@Override
	public CompletableFuture<CacheEntryType> type(String key) {
		this.startOperation();
		return this.commands.type(key(key)).toCompletableFuture()

				.thenCompose(r -> {
					return updateKeyExpiry(key(key)).thenApply(s -> r);
				}).thenApply(r -> {
					this.finishOperation();

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
				});
	}

	@Override
	public CompletableFuture<Object> get(String key) {
		this.startOperation();
		return this.commands.get(key(key)).toCompletableFuture().thenCompose(r -> {
			return updateKeyExpiry(key(key)).thenApply(s -> r);
		}).thenApply(r -> {
			this.finishOperation();
			return r;
		});
	}

	@Override
	public CompletableFuture<Object> set(String key, Object value) {
		this.startOperation();
		RedisFuture<String> o = this.commands.set(key(key), value);

		return o.toCompletableFuture()

				.thenCompose(r -> {
					return updateKeyExpiry(key(key)).thenApply(s -> r);
				}).thenApply(r -> {
					this.finishOperation();

					if (!r.equals("OK")) {
						Exceptions.throwRuntime(
								new CacheException("Error occured during call to cache.set(..): " + o.getError()));
					}

					return value;
				});
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

		this.startOperation();
		return this.commands.del(keys_).toCompletableFuture().thenCompose(r -> removeKeyExpiry(keys_)).thenApply(r -> {
			this.finishOperation();
			return r.intValue();
		});
	}

	@Override
	public CompletableFuture<Boolean> expire(String key, Long seconds) {
		this.startOperation();
		RedisFuture<Boolean> o = this.expire0(key(key), seconds);

		return o.toCompletableFuture()

				.thenCompose(r -> removeKeyExpiry(key(key)).thenApply(s -> r))

				.thenApply(r -> {
					this.finishOperation();

					if (!r && this.strict) {
						Exceptions.throwRuntime(
								new CacheException("Error occured during call to cache.expire(..): " + o.getError()));
					}

					return r;
				});
	}

	private RedisFuture<Boolean> expire0(String key, Long seconds) {
		return this.commands.expire(key, seconds);
	}

	@Override
	public CompletableFuture<Boolean> invalidate(String key, Long seconds) {

		this.startOperation();
		RedisFuture<Boolean> o = this.expire0(key(key), seconds);

		return o.toCompletableFuture()

				.thenCompose(r -> setKeyExpiry(key(key), seconds).thenApply(s -> r))

				.thenApply(r -> {
					this.finishOperation();

					if (!r && this.strict) {
						Exceptions.throwRuntime(
								new CacheException("Error occured during call to cache.expire(..): " + o.getError()));
					}

					return r;
				});
	}

	@Override
	public CompletableFuture<Integer> incrby(String key, Integer amount) {
		this.startOperation();
		return this.commands.incrby(key(key), amount).toCompletableFuture().thenCompose(r -> {
			return updateKeyExpiry(key(key)).thenApply(s -> r);
		}).thenApply(r -> {
			this.finishOperation();
			return r.intValue();
		});
	}

	@Override
	public CompletableFuture<Integer> slength(String key) {
		this.startOperation();
		return this.commands.scard(key(key)).toCompletableFuture().thenCompose(r -> {
			return updateKeyExpiry(key(key)).thenApply(s -> r);
		}).thenApply(r -> {
			this.finishOperation();
			return r.intValue();
		});
	}

	@Override
	public CompletableFuture<?> sget(String key, Function<Object, CompletableFuture<?>> consumer) {
		this.startOperation();

		return this.commands.smembers(key(key)).toCompletableFuture().thenCompose(r -> {
			return updateKeyExpiry(key(key)).thenApply(s -> r);
		}).thenAccept(r -> {
			this.finishOperation();

			List<CompletableFuture<?>> futures = new ArrayList<>(this.limit);

			r.forEach(e -> {
				futures.add(consumer.apply(e));
			});

			// Wait for all consumers in this batch to complete
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
		});
	}

	@Override
	public CompletableFuture<Integer> sadd(String key, List<Object> elements) {
		this.startOperation();
		Object[] elementsArray = elements.toArray(new Object[elements.size()]);
		return this.commands.sadd(key(key), elementsArray).toCompletableFuture().thenCompose(r -> {
			return updateKeyExpiry(key(key)).thenApply(s -> r);
		}).thenApply(r -> {
			this.finishOperation();
			return r.intValue();
		});
	}

	@Override
	public CompletableFuture<Integer> sdel(String key, Object value) {
		return sdel(key, new Object[] { value });
	}

	@Override
	public CompletableFuture<Integer> sdel(String key, Object... values) {
		this.startOperation();
		return this.commands.srem(key(key), values).toCompletableFuture().thenCompose(r -> {
			return updateKeyExpiry(key(key)).thenApply(s -> r);
		}).thenApply(r -> {
			this.finishOperation();
			return r.intValue();
		});
	}

	@Override
	public CompletableFuture<Object> hset(String key, String field, Object value) {
		this.startOperation();
		return this.hset0(key(key), field, value).thenCompose(r -> {
			return updateKeyExpiry(key(key)).thenApply(s -> r);
		}).thenApply(r -> {
			this.finishOperation();

			if (!r && this.strict) {
				Exceptions.throwRuntime(
						new CacheException("An entry with field: " + field + " already exists in hash: " + key));
			}

			return r;
		});
	}

	private CompletableFuture<Boolean> hset0(String key, String field, Object value) {
		return this.commands.hset(key, field, value).toCompletableFuture();
	}

	@Override
	public CompletableFuture<Object> hget(String key, String field) {
		this.startOperation();
		return this.hget0(key(key), field).thenCompose(r -> {
			return updateKeyExpiry(key(key)).thenApply(s -> r);
		}).thenApply(r -> {
			this.finishOperation();
			return r;
		});
	}

	private CompletableFuture<Object> hget0(String key, String field) {
		return this.commands.hget(key, field).toCompletableFuture();
	}

	@Override
	public CompletableFuture<Map<String, Object>> hgetall(String key) {
		this.startOperation();
		return this.commands.hgetall(key(key)).toCompletableFuture().thenCompose(r -> {
			return updateKeyExpiry(key(key)).thenApply(s -> r);
		}).thenApply(r -> {
			this.finishOperation();
			return r;
		});
	}

	@Override
	public CompletableFuture<Integer> hdel(String key, String field) {
		return hdel(key, new String[] { field });
	}

	@Override
	public CompletableFuture<Integer> hdel(String key, String... fields) {
		this.startOperation();
		return this.hdel0(key(key), fields).thenCompose(r -> {
			return updateKeyExpiry(key(key)).thenApply(s -> r);
		}).thenApply(r -> {
			this.finishOperation();
			return r.intValue();
		});
	}

	private CompletableFuture<Integer> hdel0(String key, String... fields) {
		return this.commands.hdel(key, fields).toCompletableFuture().thenApply(r -> r.intValue());
	}

	@Override
	public CompletableFuture<?> hkeys(String key, Function<String, CompletableFuture<?>> consumer) {

		return this.hkeys(key).thenAccept(r -> {

			List<CompletableFuture<?>> futures = new ArrayList<>(r.size());
			r.forEach(k -> {
				futures.add(consumer.apply(k));
			});

			CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
		});
	}

	@Override
	public CompletableFuture<List<String>> hkeys(String key) {
		this.startOperation();
		return this.commands.hkeys(key(key)).toCompletableFuture().thenCompose(r -> {
			return updateKeyExpiry(key(key)).thenApply(s -> r);
		}).thenApply(r -> {
			this.finishOperation();
			return r;
		});
	}

	@Override
	public CompletableFuture<Integer> hincrby(String key, String field, Integer amount) {
		this.startOperation();
		return this.commands.hincrby(key(key), field, amount).toCompletableFuture().thenCompose(r -> {
			return updateKeyExpiry(key(key)).thenApply(s -> r);
		}).thenApply(r -> {
			this.finishOperation();
			return r.intValue();
		});
	}

	@Override
	public CompletableFuture<Integer> hlen(String key) {
		this.startOperation();
		return this.commands.hlen(key(key)).toCompletableFuture().thenCompose(r -> {
			return updateKeyExpiry(key(key)).thenApply(s -> r);
		}).thenApply(r -> {
			this.finishOperation();
			return r.intValue();
		});
	}

	@Override
	CompletableFuture<Void> close() {
		this.connection.flushCommands();
		return this.connection.closeAsync();
	}

}
