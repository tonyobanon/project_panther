package com.re.paas.internal.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.re.paas.api.cache.Cache;
import com.re.paas.api.cache.Checkpoint;
import com.re.paas.api.cache.PartitionedSet;
import com.re.paas.api.cache.PersistenceType;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.async.RedisAsyncCommands;

public class DefaultCache implements Cache<String, String> {

	private static final String keyLength = "_";
	private static final String missesCount = "_mc";
	private static final String valueKeyMappingPrefix = "_vkmp_";

	private String instanceId;
	RedisAsyncCommands<String, String> commands;

	private int limit;
	private boolean greedy;

	DefaultCache() {
		this(null);
	}

	DefaultCache(String instanceId) {

		this.instanceId = instanceId;
		this.commands = createRedisClient().connect().async();

		this.reset();
	}

	private void incrStack() {
		if (instanceId != null) {
			DefaultCacheFactory.incrStack(instanceId);
		}
	}

	private void decrStack() {
		if (instanceId != null) {
			DefaultCacheFactory.decrStack(instanceId);
		}
	}

	private static RedisClient createRedisClient() {

		StringBuilder credentials = new StringBuilder();
		CacheConfig config = CacheConfig.get();

		if (config.getUsername() != null && config.getPassword() != null) {
			credentials.append(config.getUsername()).append(":").append(config.getPassword()).append("@");
		}

		StringBuilder uri = new StringBuilder().append("redis://").append(credentials).append(config.getHost())
				.append(":").append(config.getPort()).append("/").append(config.getDatabase());

		return RedisClient.create(uri.toString());
	}

	@Override
	public void reset() {
		this.limit = -1;
		this.greedy = false;
	}

	@Override
	public void limit(int limit) {
		this.limit = limit;
	}

	@Override
	public void greedy() {
		this.greedy = true;
	}

	@Override
	public CompletableFuture<PartitionedSet<String>> sget(String key, Checkpoint checkpoint) {
		return this.sgetOrDefault(null, key, checkpoint, null);
	}

	@Override
	public CompletableFuture<PartitionedSet<String>> sgetOrDefault(PersistenceType persistenceType, String key,
			Checkpoint checkpoint, Callable<List<String>> producer) {

		if (checkpoint == null) {
			checkpoint = new Checkpoint();
			checkpoint.setStart(0);
		}

		if (checkpoint.getStart() == null || checkpoint.getStart() < 0) {
			throw new RuntimeException("Invalid value for checkpoint.start provided");
		}

		ObjectWrapper<Integer> checkpointStart = new ObjectWrapper<Integer>(checkpoint.getStart());
		ObjectWrapper<Integer> checkpointEnd = new ObjectWrapper<Integer>(checkpoint.getEnd());

		ObjectWrapper<List<String>> data = new ObjectWrapper<List<String>>();
		ObjectWrapper<Checkpoint> next = new ObjectWrapper<Checkpoint>();

		Callable<CompletionStage<Object>> work = () -> {

			this.incrStack();

			// Get key length
			String len = this.hget(key, keyLength).join();
			int totalLength = len != null ? Integer.parseInt(len) : 0;

			if (totalLength == 0) {

				data.set(Collections.emptyList());

				// Get data from producer

				if (producer != null) {
					try {
						data.set(producer.call());
					} catch (Exception e) {
						throw new RuntimeException(Exceptions.recurseCause(e));
					}

					this.sadd(persistenceType, key, data.get());
				}

				next.set(new Checkpoint());
				return CompletableFuture.completedFuture(null);
			}

			int limit = this.limit > 0 ? this.limit : totalLength;

			if (checkpointStart.get() > (totalLength - 1)) {
				throw new RuntimeException("IndexOutOfBounds exception for checkpoint.start => " + checkpointStart);
			}

			if (checkpointEnd.get() == null) {
				checkpointEnd.set(checkpointStart.get() + (limit - 1));
			}

			if (checkpointEnd.get() < 0 || checkpointEnd.get() > (totalLength - 1)) {
				checkpointEnd.set(totalLength - 1);
			}

			List<CompletableFuture<?>> futures = new ArrayList<>(limit * 2);
			List<String> array = new ArrayList<>(limit * 2);

			ObjectWrapper<Callable<CompletionStage<Object>>> fetch = new ObjectWrapper<Callable<CompletionStage<Object>>>();

			fetch.set(() -> {

				for (Integer i = checkpointStart.get(); i <= checkpointEnd.get(); i++) {

					CompletableFuture<?> future = this.hget(key, i.toString()).thenAccept(v -> {

						if (v != null) {
							array.add(v);
						} else {
							// entry may have been deleted in sdel(..), or
							// may have been skipped due to possible duplicates in sadd(..)
						}

					});
					futures.add(future);
				}

				return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
						.thenCompose((r) -> {

							if (!this.greedy) {
								return CompletableFuture.completedFuture(null);
							}

							// <offset> elements had empty slots
							int offset = futures.size() - array.size();

							if (offset > 0) {

								// consolidate <array>, inorder to have the same length with <promises>

								for (int i = 0; i < offset; i++) {
									array.add(null);
								}

								if ((checkpointEnd.get() + offset) > (totalLength - 1)) {
									// We need to resize offset, such that end + offset <= totalLength - 1
									offset = (totalLength - checkpointEnd.get()) - 1;
								}

								if (offset > 0) {

									// We need to recurse inorder to fetch <offset> elements
									checkpointStart.set(checkpointEnd.get() + 1);
									checkpointEnd.set(checkpointEnd.get() + offset);

									try {
										return fetch.get().call();
									} catch (Exception e) {
										throw new RuntimeException(e);
									}
								}
							}

							// prune array
							array.removeIf(e -> e == null);

							return CompletableFuture.completedFuture(null);
						});
			});

			CompletionStage<Object> result = null;

			try {
				result = fetch.get().call();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			return result.thenCompose((r) -> {

				this.decrStack();

				Integer nextStart = null;
				Integer nextEnd = null;

				if (checkpointEnd.get() + 1 < totalLength) {

					int range = checkpointEnd.get() - checkpointStart.get();

					nextStart = checkpointEnd.get() + 1;
					nextEnd = nextStart + range;

					if (nextEnd > (totalLength - 1)) {
						nextEnd = totalLength - 1;
					}
				}

				data.set(array);
				next.set(new Checkpoint(nextStart, nextEnd));

				return CompletableFuture.completedFuture(null);
			});
		};

		CompletableFuture<Object> result = null;
		try {
			result = (CompletableFuture<Object>) work.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return CompletableFuture.allOf(result)
				.thenApply((x) -> new PartitionedSet<String>().setData(data.get()).setNext(next.get()));
	}

	@Override
	public CompletableFuture<Boolean> sadd(PersistenceType persistenceType, String key, List<String> elements) {

		Callable<CompletionStage<Boolean>> work = () -> {

			this.incrStack();

			if (key.startsWith(valueKeyMappingPrefix)) {
				throw new Error("key cannot start with '" + valueKeyMappingPrefix + "'");
			}

			// Get key length
			String len = this.hget(key, keyLength).join();
			int totalLength = len != null ? Integer.parseInt(len) : 0;

			Integer currentLength = elements.size();

			for (Integer i = totalLength; i < totalLength + currentLength; i++) {

				String k = i.toString();
				String v = elements.get(i - totalLength);

				if (this.hget(valueKeyMappingPrefix + key, v).join() != null) {
					// value already exist, continue to avoid duplicates
					this.hincrby(key, missesCount, 1L);
					continue;
				}

				// map keys to values
				this.hset(key, k, v);

				// map values to keys
				this.hset(valueKeyMappingPrefix + key, v, k);
			}

			if (totalLength == 0) {
				this.expire(key, persistenceType);
				this.expire(valueKeyMappingPrefix + key, persistenceType);
			}

			this.hincrby(key, keyLength, currentLength.longValue()).join();

			this.decrStack();

			return CompletableFuture.completedFuture(true);
		};

		CompletableFuture<Boolean> result = null;
		try {
			result = (CompletableFuture<Boolean>) work.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return CompletableFuture.anyOf(result).thenApply(r -> (Boolean) r);
	}

	@Override
	public CompletableFuture<Boolean> sdel(String key, String value) {

		Callable<CompletionStage<Boolean>> work = () -> {

			this.incrStack();

			String k = this.hget(valueKeyMappingPrefix + key, value).join();

			if (k == null) {
				// No entry exists with this value
				return CompletableFuture.completedFuture(false);
			}

			this.hdel(valueKeyMappingPrefix + key, value);
			this.hdel(key, k);

			this.hincrby(key, missesCount, 1L);

			// Note that the declared set size does not change, i.e esk

			// Note that: we cannot call hincrby(key, esk, -1) because k can be at
			// any random position in the hash

			this.decrStack();

			return CompletableFuture.completedFuture(true);
		};

		CompletableFuture<Boolean> result = null;
		try {
			result = (CompletableFuture<Boolean>) work.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return CompletableFuture.anyOf(result).thenApply(r -> (Boolean) r);
	}

	@Override
	public CompletableFuture<Boolean> mset(PersistenceType persistenceType, String key, Map<String, String> value) {

		Callable<CompletionStage<Void>> work = () -> {

			this.incrStack();

			List<CompletableFuture<?>> futures = new ArrayList<>(value.size());

			value.forEach((k, v) -> {
				futures.add(this.hset(key, k, v));
			});

			Long hLen = this.hlen(key).get();

			// Determine whether to set expiry on hash
			if (hLen <= value.size()) {
				this.expire(key, persistenceType);
			}

			return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).thenRun(() -> {
				this.decrStack();
			});
		};

		CompletableFuture<Void> result = null;
		try {
			result = (CompletableFuture<Void>) work.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return CompletableFuture.allOf(result).thenApply(r -> true);
	}

	public CompletableFuture<Boolean> expire(String key, PersistenceType persistenceType) {
		Integer expiration = persistenceType.getExpiry();
		if (expiration != null) {
			return this.expire(key, expiration.longValue());
		}
		return CompletableFuture.completedFuture(true);
	}

	@Override
	public CompletableFuture<String> get(String key) {
		this.incrStack();
		@SuppressWarnings("unchecked")
		CompletableFuture<String> o = (CompletableFuture<String>) this.commands.get(key);
		this.decrStack();
		return o;
	}

	@Override
	public CompletableFuture<String> set(String key, String value) {
		this.incrStack();
		@SuppressWarnings("unchecked")
		CompletableFuture<String> o = (CompletableFuture<String>) this.commands.set(key, value);
		this.decrStack();
		return o;
	}

	@Override
	public CompletableFuture<Long> del(String... keys) {
		this.incrStack();
		@SuppressWarnings("unchecked")
		CompletableFuture<Long> o = (CompletableFuture<Long>) this.commands.del(keys);
		this.decrStack();
		return o;
	}

	@Override
	public CompletableFuture<Boolean> hset(String key, String field, String value) {
		this.incrStack();
		@SuppressWarnings("unchecked")
		CompletableFuture<Boolean> o = (CompletableFuture<Boolean>) this.commands.hset(key, field, value);
		this.decrStack();
		return o;
	}

	@Override
	public CompletableFuture<String> hget(String key, String field) {
		this.incrStack();
		@SuppressWarnings("unchecked")
		CompletableFuture<String> o = (CompletableFuture<String>) this.commands.hget(key, field);
		this.decrStack();
		return o;
	}

	@Override
	public CompletableFuture<Map<String, String>> hgetall(String key) {
		this.incrStack();
		@SuppressWarnings("unchecked")
		CompletableFuture<Map<String, String>> o = (CompletableFuture<Map<String, String>>) this.commands.hgetall(key);
		this.decrStack();
		return o;
	}

	@Override
	public CompletableFuture<Long> hdel(String key, String... fields) {
		this.incrStack();
		@SuppressWarnings("unchecked")
		CompletableFuture<Long> o = (CompletableFuture<Long>) this.commands.hdel(key, fields);
		this.decrStack();
		return o;
	}

	@Override
	public CompletableFuture<List<String>> hkeys(String key) {
		this.incrStack();
		@SuppressWarnings("unchecked")
		CompletableFuture<List<String>> o = (CompletableFuture<List<String>>) this.commands.hkeys(key);
		this.decrStack();
		return o;
	}

	@Override
	public CompletableFuture<Long> hincrby(String key, String field, Long amount) {
		this.incrStack();
		@SuppressWarnings("unchecked")
		CompletableFuture<Long> o = (CompletableFuture<Long>) this.commands.hincrby(key, field, amount);
		this.decrStack();
		return o;
	}

	@Override
	public CompletableFuture<Long> hlen(String key) {
		this.incrStack();
		@SuppressWarnings("unchecked")
		CompletableFuture<Long> o = (CompletableFuture<Long>) this.commands.hlen(key);
		this.decrStack();
		return o;
	}

	@Override
	public CompletableFuture<Boolean> expire(String key, Long seconds) {
		this.incrStack();
		@SuppressWarnings("unchecked")
		CompletableFuture<Boolean> o = (CompletableFuture<Boolean>) this.commands.expire(key, seconds);
		this.decrStack();
		return o;
	}

	@Override
	public CompletableFuture<Boolean> quit() {
		this.incrStack();
		@SuppressWarnings("unchecked")
		CompletableFuture<String> o = (CompletableFuture<String>) this.commands.quit();
		this.decrStack();
		return o.thenApply(r -> r.equals("OK"));
	}

}
