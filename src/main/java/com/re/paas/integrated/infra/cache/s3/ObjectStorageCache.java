package com.re.paas.integrated.infra.cache.s3;

import java.nio.ByteBuffer;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.IntegerWrapper;
import com.re.paas.api.classes.ObjectSerializer;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.infra.cache.AbstractCache;
import com.re.paas.api.infra.cache.CacheEntryType;
import com.re.paas.api.infra.cache.CacheException;
import com.re.paas.api.infra.cache.Checkpoint;
import com.re.paas.api.infra.cache.PartitionedSet;
import com.re.paas.api.utils.Dates;
import com.re.paas.internal.serialization.Primitives;

/**
 * This class provides a polyfill that enables a conventional key value store to
 * be accessed like a full blown cache server with support for hashes and sets.
 * Note: due to the extra metadata overhead, the storage requirements could be
 * much larger than a conventional cache server that has in-built support for
 * sets and hashes <br>
 * 
 * The advantage is that any arbitrary object storage service (i.e wasabi) can
 * be used as a cache server
 * 
 * <br>
 * 
 * @author anthony.anyanwu
 *
 */

@BlockerTodo("Here, I often assert the entry type i.e primitive instead of playing along with whatever type exists already")
@BlockerTodo("Remove all occurences of future.join(), and replace with async equivalent")
@BlockerTodo("Adds support for sets rebalancing (inorder to reset misses), for optimized performance")
@BlockerTodo("Add support for multi-tenancy")

public abstract class ObjectStorageCache extends AbstractCache {

	// Constants used in hashes
	private static final String hashDelim = "$$$";
	private static final String keysSuffix = "_kys";

	// Constants used in sets
	private static final String keyWorkingLength = "_";
	private static final String missesCount = "_mc";

	private static final String keyFieldMappingPrefix = "_kvmp_";
	private static final String keyValueMappingPrefix = "_vkmp_";

	// Generic constants
	private static final String entryTypeSuffix = "_ets";

	/**
	 * {@link #keyInvalidationHash} is private to {@link ObjectStorageCache},
	 * and is used to keep track of entries for which an expiration/inactivity
	 * timeout has been set
	 */
	// k: "abc", v: "(i123|e)#Date" , e = expire, i = inactivity
	static final String keyExpiryBucketSuffix = "__$kis";

	private static final Integer defaultLimit = 100;
	private static final Integer maxLimit = 100000;

	private Integer limit;
	private Boolean strict;

	protected Boolean greedy;

	public ObjectStorageCache() {
		
		this.limit = defaultLimit;
		this.strict = false;
		
		this.greedy = true;
	}

	/**
	 * This returns the configured limit for this instance. Limits are generally
	 * used across operations for pagination purpose
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * By default, there is a limit of {@link AbstractCache#defaultLimit}. Use this
	 * method to set a custom value. If you want to fetch all values at once, use a
	 * value of -1
	 */
	public final void setLimit(int limit) {
		 
		if (limit == 0 || limit > maxLimit) {
			Exceptions.throwRuntime("The allowed value range for limit is: 1 - " + maxLimit);
		}
		
		this.limit = limit;
	}
	
	public Boolean isGreedy() {
		return greedy;
	}

	public ObjectStorageCache setGreedy(Boolean greedy) {
		this.greedy = greedy;
		return this;
	}

	/**
	 * Each time an activity happens on a key, and the key is set to be evicted by
	 * inactivity, then we need to refresh the expiry
	 * 
	 * @param key
	 * @return
	 */
	private CompletableFuture<Boolean> updateKeyExpiry(String key) {

		return this.getExpiryCache().doGet(key).thenCompose(r -> {

			if (r == null) {
				// Neither invalidation or expiry has been configured for key
				return null;
			}

			String v = new String(r.array());

			if (!v.substring(0, 1).equals("i")) {
				return null;
			}

			String arr[] = v.split("#", 1);

			Long seconds = Long.parseLong(arr[0].substring(1));

			Date now = Dates.now();
			Date expiryDate = Dates.toDate(arr[1]);
			
			if(now.toInstant().isAfter(expiryDate.toInstant())) {
				
				// This key is already invalidated, but it seems that the reaper has not deleted
				// So we will not extend the time, so it can be removed by the reaper on next run
				
				return null;
			}
			
			// Reset invalidation time
			return invalidate0(key, seconds, expiryDate);
		});
	}

	/**
	 * This removes the specified key(s) from keyInvalidationStatHash, if necessary
	 * 
	 * @param keys
	 * @return
	 */
	private CompletableFuture<?> setKeyExpiry(String key, String value) {
		return this.getExpiryCache().doSet(key, ByteBuffer.wrap(value.getBytes()));
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
			futures[i] = this.getExpiryCache().doDelete(keys[i]);
		}
		return CompletableFuture.allOf(futures).thenApply(r -> keys.length);
	}
	
	protected abstract ObjectStorageCache getExpiryCache();

	/**
	 * If the specified key does not exist as a set, then the value 0 is returned
	 */
	@Override
	public final CompletableFuture<Integer> slength(String key) {
		return slength0(key).thenCompose(r -> {
			return updateKeyExpiry(key).thenApply(s -> r);
		});
	}

	private CompletableFuture<Integer> slength0(String key) {

		return

		// Get working length
		asIntegerFuture(this.hget0(key, keyWorkingLength)).thenCombine(

				// Get misses
				asIntegerFuture(this.hget0(key, missesCount)), (len, misses) -> {

					// Return difference
					return len - misses;
				});
	}

	/**
	 * 
	 * @param future It is generally assumed that this resolves to a String
	 * @return
	 */
	private static final CompletableFuture<Integer> asIntegerFuture(CompletableFuture<?> future) {
		return future.thenApply((r) -> {
			return r != null ? Integer.parseInt((String) r) : 0;
		});
	}

	@Override
	public CompletableFuture<?> sget(String key, Function<Object, CompletableFuture<?>> consumer) {

		ObjectWrapper<CacheEntryType> type = new ObjectWrapper<>();

		return assertKeyType(key, CacheEntryType.SET).thenAccept(r -> {
			type.set(r);
		}).thenRun(() -> {
			this.sget0(key, consumer, CompletableFuture.completedFuture(type.get() != null));
		}).thenRun(() -> {
			updateKeyExpiry(key);
		});
	}

	private void sget0(String key, Function<Object, CompletableFuture<?>> consumer,
			CompletableFuture<Boolean> keyExists) {

		PartitionedSet<Object> current = new PartitionedSet<>();

		while ((current = this.sget0(key, current.getNext(), keyExists).join()).getNext().getStart() != null) {

			if (this.limit > 0) {

				// We want to wait for the execution of all consumers in this batch

				List<CompletableFuture<?>> futures = new ArrayList<>(this.limit);

				for (int i = 0; i < limit; i++) {

					try {

						Object v = current.getData().get(i);
						futures.add(consumer.apply(v));

					} catch (IndexOutOfBoundsException e) {

						// If this happens, it's one of two things that happened

						// 1. There is no more element in this set
						// 2. This cache instance is not greedy, irrespective of whether there is data
						// or not remaining

						break;
					}
				}

				// Wait for all consumers in this batch to complete
				CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();

			} else {
				current.getData().forEach(e -> consumer.apply(e));
			}
		}
	}

	private CompletableFuture<PartitionedSet<Object>> sget0(String key, Checkpoint checkpoint,
			CompletableFuture<Boolean> keyExists) {
		return this.sgetOrDefault(key, checkpoint, null, keyExists);
	}

	private CompletableFuture<PartitionedSet<Object>> sgetOrDefault(String key, Checkpoint checkpoint,
			Callable<List<Object>> producer, CompletableFuture<Boolean> keyExists) {

		if (checkpoint == null) {
			checkpoint = new Checkpoint();
			checkpoint.setStart(0);
		}

		if (checkpoint.getStart() == null || checkpoint.getStart() < 0) {
			throw new RuntimeException("Invalid value for checkpoint.start provided");
		}

		ObjectWrapper<Integer> checkpointStart = new ObjectWrapper<Integer>(checkpoint.getStart());
		ObjectWrapper<Integer> checkpointEnd = new ObjectWrapper<Integer>(checkpoint.getEnd());

		ObjectWrapper<List<Object>> data = new ObjectWrapper<List<Object>>();
		ObjectWrapper<Checkpoint> next = new ObjectWrapper<Checkpoint>();

		Callable<CompletionStage<Object>> work = () -> {

			// Get working key length
			Integer totalLength = asIntegerFuture(this.hget0(key, keyWorkingLength)).join();

			if (totalLength == 0) {

				data.set(Collections.emptyList());

				// Get data from producer

				if (producer != null) {
					try {
						data.set(producer.call());
					} catch (Exception e) {
						throw new RuntimeException(Exceptions.recurseCause(e));
					}

					this.sadd(key, data.get(), keyExists);
				}

				next.set(new Checkpoint());
				return CompletableFuture.completedFuture(null);
			}

			// Check set length, by checking misses

			// Alternatively, we can call this.hlen(...), but we wanted to avoid fetching
			// <keyWorkingLength> again

			Integer misses = asIntegerFuture(this.hget0(key, missesCount)).join();

			if (totalLength - misses == 0) {
				return CompletableFuture.completedFuture(null);
			}

			Integer limit = this.limit > 0 ? this.limit : totalLength;

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
			List<Object> array = new ArrayList<>(limit * 2);

			ObjectWrapper<Callable<CompletionStage<Object>>> fetch = new ObjectWrapper<Callable<CompletionStage<Object>>>();

			fetch.set(() -> {

				for (Integer i = checkpointStart.get(); i <= checkpointEnd.get(); i++) {

					CompletableFuture<?> future = this.get0(keyFieldhashKey(key, i.toString())).thenAccept(v -> {

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
				.thenApply((x) -> new PartitionedSet<Object>().setData(data.get()).setNext(next.get()));
	}

	@Override
	public final CompletableFuture<Integer> sadd(String key, List<Object> elements) {

		if (elements.isEmpty()) {
			Exceptions.throwRuntime(new CacheException("No element(s) exist for set: " + key));
		}

		return assertKeyType(key, CacheEntryType.SET)
				.thenCompose(type -> sadd(key, elements, CompletableFuture.completedFuture(type != null)))
				.thenCompose(r -> {
					return updateKeyExpiry(key).thenApply(s -> r);
				});
	}

	@BlockerTodo("Create private method, and indirect this. Why always computing slength?")
	private CompletableFuture<Integer> sadd(String key, List<Object> elements, CompletableFuture<Boolean> keyExists) {

		// Only primitive types are allowed

		for (int i = 0; i < elements.size(); i++) {
			Object e = elements.get(i);
			if (!Primitives.isWrapperType(e)) {
				Exceptions.throwRuntime(new CacheException(
						"The non-primitive object: " + e + " at index " + i + " can be added to this set"));
			}
		}

		Callable<CompletionStage<Integer>> work = () -> {

			Boolean exists = keyExists.join();

			validateHashkey(key);

			// Get working key length
			Integer totalLength = asIntegerFuture(this.hget0(key, keyWorkingLength)).join();

			assert (totalLength == 0) == exists;

			Integer currentLength = elements.size();

			List<CompletableFuture<?>> futures = new ArrayList<>();

			for (Integer i = totalLength; i < totalLength + currentLength; i++) {

				String k = i.toString();
				Object v = elements.get(i - totalLength);

				if (this.get0(keyValuehashKey(key, v)).join() != null) {

					// value already exist, continue to avoid duplicates

					// The reason this is done, is because, we need to account for the current index
					// (i) which has just been skipped because a duplicate was found

					this.hincrby0(key, missesCount, 1, false, null);

					continue;
				}

				futures.add(addHashEntryForSet(key, k, v));
			}

			if (!exists) {

				// Since this is a new set, register key type
				addKeyType(key, CacheEntryType.SET);
			}

			return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).thenCompose((r) ->

			this.hincrby0(key, keyWorkingLength, currentLength, false, null)

					.thenCombine(asIntegerFuture(this.hget0(key, missesCount)), (workingLength, misses) -> {
						return workingLength - misses;
					}));
		};

		CompletableFuture<Integer> result = null;
		try {
			result = (CompletableFuture<Integer>) work.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	@BlockerTodo("This method just returns the number of attempts made, rather than the actual deletes that went through")
	@Override
	public CompletableFuture<Integer> sdel(String key, Object... values) {

		List<CompletableFuture<?>> futures = new ArrayList<>(values.length);

		for (Object value : values) {
			futures.add(sdel0(key, value));
		}

		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
				.thenApply(r -> values.length).thenCompose(r -> {
					return updateKeyExpiry(key).thenApply(s -> r);
				});
	}

	@BlockerTodo("This should return the elements removed and not slength!")
	@Override
	public final CompletableFuture<Integer> sdel(String key, Object value) {
		return sdel0(key, value).thenCompose(r -> {
			return updateKeyExpiry(key).thenApply(s -> r);
		});
	}

	private CompletableFuture<Integer> sdel0(String key, Object value) {

		Callable<CompletionStage<Integer>> work = () -> {

			String k = (String) this.get0(keyValuehashKey(key, value)).join();

			if (k == null) {

				// No entry exists with this value

				if (this.strict) {
					Exceptions.throwRuntime(new IllegalArgumentException(
							"No entry exists with this value: " + value + " in the set: " + key));
				}

				return CompletableFuture.completedFuture(-1);
			}

			removeHashEntryForSet(key, k, value);

			return this.hincrby0(key, missesCount, 1, false, null)

					.thenCombine(asIntegerFuture(this.hget0(key, keyWorkingLength)), (misses, workingLength) -> {
						return workingLength - misses;
					});
		};

		CompletableFuture<Integer> result = null;
		try {
			result = (CompletableFuture<Integer>) work.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	private static void validateHashkey(String key) {

		if (key.startsWith(keyValueMappingPrefix) || key.startsWith(keyFieldMappingPrefix)
				|| key.endsWith(keysSuffix)) {

			Exceptions.throwRuntime(new IllegalArgumentException("Key: '" + key + "' is not allowed."));
		}
	}

	/**
	 * This validates that the specified key can be mapped to a primitive
	 * 
	 * @param key
	 */
	private static void validateEntry(String key) {

		validateHashkey(key);

		if (key.endsWith(entryTypeSuffix) || key.endsWith(keyWorkingLength) || key.endsWith(missesCount)) {
			Exceptions.throwRuntime(new IllegalArgumentException("Key: '" + key + "' is not allowed."));
		}

		if (key.length() == 0) {
			Exceptions.throwRuntime(new IllegalArgumentException("Key must not be empty."));
		}
	}

	private final CompletableFuture<?> addHashEntryForSet(String key, String field, Object value) {

		return CompletableFuture.allOf(hset0(keyFieldMappingPrefix + key, field, value),
				hset0(keyValueMappingPrefix + key, Primitives.toString(value), field));
	}

	private final CompletableFuture<?> removeHashEntryForSet(String key, String field, Object value) {
		return CompletableFuture.allOf(hdel0(keyValueMappingPrefix + key, Primitives.toString(value)),
				hdel0(keyFieldMappingPrefix + key, field));
	}

	private final String keyFieldhashKey(String key, String field) {
		return join0(keyFieldMappingPrefix + key, field);
	}

	private final String keyValuehashKey(String key, Object value) {
		return join0(keyValueMappingPrefix + key, Primitives.toString(value));
	}

	@Override
	public final CompletableFuture<Object> hset(String key, String field, Object value) {

		validateEntry(join0(key, field));

		return assertKeyType(key, CacheEntryType.HASH)

				.thenCompose(type -> hset(key, field, value, CompletableFuture.completedFuture(type != null),
						CompletableFuture.completedFuture(type != null && hget0(key, field).join() != null)))

				.thenCompose(r -> {
					return updateKeyExpiry(key).thenApply(s -> r);
				});
	}

	private CompletableFuture<Object> hset(String key, String field, Object value, CompletableFuture<Boolean> keyExists,
			CompletableFuture<Boolean> fieldExists) {

		return this.hset0(key, field, value).thenApply(v -> {

			assert v == value;

			if (!fieldExists.join()) {

				List<CompletableFuture<?>> futures = new ArrayList<>(2);

				if (!keyExists.join()) {

					// This is a new hash
					// register key type
					futures.add(addKeyType(key, CacheEntryType.HASH));
				}

				// update keys set
				futures.add(this.sadd(keysSetOfHash(key), Arrays.asList(field), keyExists));

				futures.toArray(new CompletableFuture[futures.size()]);
			}

			return v;
		});
	}

	private CompletableFuture<Object> hset0(String key, String field, Object value) {
		return this.set0(join0(key, field), value);
	}

	private static String join0(String key, String field) {
		return key + hashDelim + field;
	}

	/**
	 * This returns the set key, for the given hash, where keys are stores
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	private static String keysSetOfHash(String key) {
		return key + keysSuffix;
	}

	@BlockerTodo("This method just returns the number of attempts made, rather than the actual deletes that went through")
	@Override
	public CompletableFuture<Integer> hdel(String key, String... fields) {

		List<CompletableFuture<?>> futures = new ArrayList<>(fields.length);

		for (String field : fields) {
			CompletableFuture<?> future = hdel0(key, field).thenCompose((r) -> this.sdel0(keysSetOfHash(key), field));
			futures.add(future);
		}

		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
				.thenApply(r -> fields.length).thenCompose(r -> {
					return updateKeyExpiry(key).thenApply(s -> r);
				});
	}

	@BlockerTodo("This should return the elements removed and not slength!")
	@Override
	public final CompletableFuture<Integer> hdel(String key, String field) {
		return hdel0(key, field).thenCompose((r) -> this.sdel0(keysSetOfHash(key), field)).thenCompose(r -> {
			return updateKeyExpiry(key).thenApply(s -> r);
		});
	}

	private CompletableFuture<?> hdel0(String key, String field) {
		return this.doDelete(join0(key, field));
	}

	@Override
	public final CompletableFuture<Integer> hlen(String key) {
		return this.slength0(keysSetOfHash(key)).thenCompose(r -> {
			return updateKeyExpiry(key).thenApply(s -> r);
		});
	}

	@Override
	public final CompletableFuture<Object> hget(String key, String field) {
		return this.hget0(key, field).thenCompose(r -> {
			return updateKeyExpiry(key).thenApply(s -> r);
		});
	}

	private CompletableFuture<Object> hget0(String key, String field) {
		return this.get0(join0(key, field));
	}

	@Override
	public final CompletableFuture<Map<String, Object>> hgetall(String key) {

		return

		assertKeyType(key, CacheEntryType.HASH)

				.thenCompose(type -> this.hkeys0(key, CompletableFuture.completedFuture(type != null)))

				.thenCompose(keys -> {

					CompletableFuture<?>[] futures = new CompletableFuture[keys.size()];
					Map<String, Object> result = new HashMap<String, Object>(keys.size());

					for (int i = 0; i < keys.size(); i++) {
						String k = keys.get(i);
						futures[i] = this.hget0(key, k).thenAccept(value -> {
							result.put(k, value);
						});
					}

					return CompletableFuture.allOf(futures).thenApply((r) -> result);
				})

				.thenCompose(r -> {
					return updateKeyExpiry(key).thenApply(s -> r);
				});
	}

	@Override
	public final CompletableFuture<?> hkeys(String key, Function<String, CompletableFuture<?>> consumer) {

		ObjectWrapper<CacheEntryType> type = new ObjectWrapper<>();

		return assertKeyType(key, CacheEntryType.HASH).thenAccept(r -> {
			type.set(r);
		}).thenRun(() -> {

			this.hkeys0(key, consumer, CompletableFuture.completedFuture(type.get() != null));

		}).thenRun(() -> {
			updateKeyExpiry(key);
		});
	}

	private final void hkeys0(String key, Function<String, CompletableFuture<?>> consumer,
			CompletableFuture<Boolean> keyExists) {

		this.sget0(keysSetOfHash(key), (k) -> {
			return consumer.apply((String) k);
		}, keyExists);
	}

	/**
	 * It should be noted that this operation may be expensive, if there is a large
	 * number of keys in this hash. The method {@link this#hkeys(String, Function)}
	 * is a better option, because for larger results, the consumer function is
	 * executed in batches
	 */
	@Override
	public final CompletableFuture<List<String>> hkeys(String key) {

		return assertKeyType(key, CacheEntryType.HASH)

				.thenCompose(type -> this.hkeys0(key, CompletableFuture.completedFuture(type != null)))

				.thenCompose(r -> {
					return updateKeyExpiry(key).thenApply(s -> r);
				});
	}

	private CompletableFuture<List<String>> hkeys0(String key, CompletableFuture<Boolean> keyExists) {

		final List<String> keys = new ArrayList<>();

		return CompletableFuture
				.runAsync(() -> this.hkeys0(key, (k) -> CompletableFuture.runAsync(() -> keys.add(k)), keyExists))
				.thenApply(r -> keys);
	}

	@Override
	public final CompletableFuture<Integer> hincrby(String key, String field, Integer amount) {

		validateEntry(join0(key, field));

		return assertKeyType(key, CacheEntryType.HASH)

				.thenCompose(type -> hincrby0(key, field, amount, true, type))

				.thenCompose(r -> {
					return updateKeyExpiry(key).thenApply(s -> r);
				});
	}

	private CompletableFuture<Integer> hincrby0(String key, String field, Integer amount, Boolean hashEntry,
			CacheEntryType keyType) {

		ObjectWrapper<String> initialValue = new ObjectWrapper<>();

		return hget0(key, field).thenApply(r -> {
			initialValue.set((String) r);

			IntegerWrapper i = new IntegerWrapper(
					initialValue.get() != null ? Integer.parseInt(initialValue.get()) : 0);

			i.add(amount);

			return i.get();

		}).thenCompose(i -> {

			if (hashEntry && initialValue.get() == null) {
				return this.hset(key, field, i.toString(), CompletableFuture.completedFuture(keyType != null),
						CompletableFuture.completedFuture(false));
			} else {
				return this.hset0(key, field, i.toString());
			}

		}).thenApply(i -> Integer.valueOf((String) i));
	}

	@Override
	public final CompletableFuture<Integer> incrby(String key, Integer amount) {

		validateEntry(key);

		return assertKeyType(key, CacheEntryType.PRIMITIVE)

				.thenCompose(type -> incrby(key, amount, type)).thenCompose(r -> {
					return updateKeyExpiry(key).thenApply(s -> r);
				});
	}

	private CompletableFuture<Integer> incrby(String key, Integer amount, CacheEntryType keyType) {

		ObjectWrapper<String> initialValue = new ObjectWrapper<>();

		return get0(key).thenApply(r -> {
			initialValue.set((String) r);

			IntegerWrapper i = new IntegerWrapper(
					initialValue.get() != null ? Integer.parseInt(initialValue.get()) : 0);

			i.add(amount);

			return i.get();

		}).thenCompose(i -> {

			if (initialValue.get() == null) {

				assert keyType == null;

				return this.set(key, i.toString(), CompletableFuture.completedFuture(false));
			} else {
				return this.set0(key, i.toString());
			}

		}).thenApply(i -> Integer.valueOf((String) i));
	}

	/**
	 * We make best effort to ensure that the cache entry with the specified key is
	 * evicted after the specified duration. However, it should be noted that in
	 * this implementation, this operation is scheduled manually on the platform,
	 * rather than on the cache server.
	 */
	@Override
	public final CompletableFuture<Boolean> expire(String key, Long seconds) {
		validateEntry(key);
		return expire0(key, seconds, exists0(key));
	}

	private CompletableFuture<Boolean> expire0(String key, Long seconds, CompletableFuture<Boolean> existsFuture) {
		return existsFuture.thenCompose(e -> {

			if (!e) {
				if (this.strict) {
					Exceptions.throwRuntime(new IllegalArgumentException("No entry exists with key: " + key));
				}
				return CompletableFuture.completedStage(false);
			}

			return setKeyExpiry(key, "e#" + Dates.toString(Dates.now(), seconds, ChronoUnit.SECONDS))
					.thenApply(r -> r != null);
		});
	}

	@Override
	public CompletableFuture<Boolean> invalidate(String key, Long seconds) {
		return this.invalidate0(key, seconds, this.exists0(key));
	}

	private CompletableFuture<Boolean> invalidate0(String key, Long seconds, CompletableFuture<Boolean> existsFuture) {
		return existsFuture.thenCompose(e -> {

			if (!e) {
				if (this.strict) {
					Exceptions.throwRuntime(new IllegalArgumentException("No entry exists with key: " + key));
				}
				return CompletableFuture.completedStage(false);
			}

			return invalidate0(key, seconds, Dates.now());
		});
	}

	private CompletableFuture<Boolean> invalidate0(String key, Long seconds, Date date) {
		return setKeyExpiry(key, "i" + seconds + "#" + Dates.toString(date, seconds, ChronoUnit.SECONDS))
				.thenApply(r -> r != null);
	}

	/**
	 * This deletes a hash
	 * 
	 * @param key
	 */
	private CompletableFuture<Void> hRemove(String key) {

		return CompletableFuture
				.runAsync(() -> this.hkeys0(key, (k) -> this.hdel0(key, k), CompletableFuture.completedFuture(true)))

				.thenCompose((r) -> CompletableFuture.allOf(

						this.sRemove(keysSetOfHash(key)),

						removeKeyType(key)));
	}

	/**
	 * This deletes a set
	 * 
	 * @param key
	 */
	private CompletableFuture<Void> sRemove(String key) {

		return CompletableFuture
				.runAsync(() -> this.sget0(key, (e) -> this.sdel0(key, e), CompletableFuture.completedFuture(true)))

				.thenCompose((r) -> CompletableFuture.allOf(

						this.hdel0(key, keyWorkingLength),

						this.hdel0(key, missesCount),

						removeKeyType(key)));
	}

	/**
	 * This deletes a primitive
	 * 
	 * @param key
	 */
	private CompletableFuture<Void> pRemove(String key) {

		return CompletableFuture.allOf(

				this.doDelete(key),

				removeKeyType(key));
	}

	@Override
	public final CompletableFuture<Boolean> exists(String key) {
		return exists0(key).thenCompose(r -> {
			return updateKeyExpiry(key).thenApply(s -> r);
		});
	}

	private CompletableFuture<Boolean> exists0(String key) {
		return getKeyType(key).thenApply(type -> {
			return type != null;
		});
	}

	@Override
	public final CompletableFuture<Object> get(String key) {
		return get0(key).thenCompose(r -> {
			return updateKeyExpiry(key).thenApply(s -> r);
		});
	}

	@Override
	public final CompletableFuture<Object> set(String key, Object value) {

		validateEntry(key);

		return assertKeyType(key, CacheEntryType.PRIMITIVE)

				.thenCompose(type -> set(key, value, CompletableFuture.completedFuture(type != null)))

				.thenCompose(r -> {
					return updateKeyExpiry(key).thenApply(s -> r);
				});
	}

	/**
	 * 
	 * It is assumed that the key provided has already been validated, before
	 * calling this function
	 * 
	 * @param key
	 * @param value
	 * @param keyExists
	 * @return
	 */
	private CompletableFuture<Object> set(String key, Object value, CompletableFuture<Boolean> keyExists) {

		return this.set0(key, value).thenApply(v -> {
			assert v == value;

			if (!keyExists.join()) {

				// This is a new entry
				// register key type

				addKeyType(key, CacheEntryType.PRIMITIVE).join();
			}

			return v;
		});
	}

	private CompletableFuture<Object> get0(String key) {
		return this.doGet(key).thenApply(buf -> ObjectSerializer.get().deserialize(buf));
	}

	private CompletableFuture<Object> set0(String key, Object value) {
		return this.doSet(key, ObjectSerializer.get().serialize(value)).thenApply(r -> value);
	}

	@BlockerTodo("This method just returns the number of attempts made, rather than the actual deletes that went through")
	@Override
	public CompletableFuture<Integer> del(String... keys) {

		List<CompletableFuture<?>> futures = new ArrayList<>(keys.length);

		for (String key : keys) {
			futures.add(del0(key));
		}

		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
				.thenCompose(r -> removeKeyExpiry(keys)).thenApply(r -> keys.length);
	}

	@Override
	public final CompletableFuture<Integer> del(String key) {
		return del0(key).thenCompose(r -> removeKeyExpiry(key));
	}

	private CompletableFuture<Integer> del0(String key) {

		return getKeyType(key)

				.thenApply(type -> {

					if (type != null) {

						CompletableFuture<Void> future = null;

						switch (type) {
						case HASH:
							future = hRemove(key);
						case PRIMITIVE:
							future = pRemove(key);
						case SET:
							future = sRemove(key);
						}

						return future;

					} else if (this.strict) {
						Exceptions.throwRuntime(
								new IllegalArgumentException("entry to be deleted: " + key + " was not found"));
					}

					return null;
				}).thenCompose(r -> {
					return removeKeyExpiry(key).thenApply(s -> r);
				}).thenApply(r -> 1);
	}

	private CompletableFuture<?> addKeyType(String key, CacheEntryType type) {
		return set0(key + entryTypeSuffix, type.toString());
	}

	private CompletableFuture<?> removeKeyType(String key) {
		return doDelete(key + entryTypeSuffix);
	}

	private CompletableFuture<CacheEntryType> assertKeyType(String key, CacheEntryType type) {

		return getKeyType(key, type, t -> {

			Exceptions.throwRuntime(
					new CacheException("A " + t.toString().toLowerCase() + " already exists with key: " + key));
		});
	}

	@Override
	public CompletableFuture<CacheEntryType> type(String key) {
		return getKeyType(key).thenCompose(r -> {
			return updateKeyExpiry(key).thenApply(s -> r);
		});
	}

	private CompletableFuture<CacheEntryType> getKeyType(String key) {
		return getKeyType(key, null, null);
	}

	private CompletableFuture<CacheEntryType> getKeyType(String key, CacheEntryType type,
			Consumer<CacheEntryType> typeConsumer) {

		return get0(key + entryTypeSuffix).thenApply((r) -> {

			CacheEntryType t = CacheEntryType.fromType((String) r);

			if (t != null && type != null && t != type) {
				typeConsumer.accept(t);
			}

			return t;
		});
	}

	/**
	 * 
	 * This returns the value mapping for the specified key or null if none exists.
	 * 
	 * @param key
	 * @return
	 */
	protected abstract CompletableFuture<ByteBuffer> doGet(String key);

	/**
	 * This sets the value mapping for the specified key.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	protected abstract CompletableFuture<?> doSet(String key, ByteBuffer value);

	/**
	 * This remove the value mapping for the specified key
	 * 
	 * @param keys
	 * @return
	 */
	protected abstract CompletableFuture<?> doDelete(String... keys);

}
