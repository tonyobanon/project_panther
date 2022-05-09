package com.re.paas.internal.infra.cache.infinispan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.infinispan.AdvancedCache;
import org.infinispan.Cache;
import org.infinispan.metadata.EmbeddedMetadata;
import org.infinispan.metadata.Metadata;
import org.infinispan.multimap.api.embedded.MultimapCache;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.KeyValuePair;
import com.re.paas.api.infra.cache.EvictableCache;
import com.re.paas.api.infra.cache.AbstractCache;
import com.re.paas.api.infra.cache.CacheEntryType;

public class InfinispanCache extends AbstractCache<String, Object> implements EvictableCache<String, Object> {

	final Cache<String, Object> cache;
	final MultimapCache<String, Object> multimapCache;

	public InfinispanCache(Cache<String, Object> cache, MultimapCache<String, Object> multimapCache) {
		this.cache = cache;
		this.multimapCache = multimapCache;
	}

	private static Map<String, Object> asMap(Collection<Object> c) {
		Map<String, Object> map = new HashMap<>();

		c.forEach(o -> {
			@SuppressWarnings("unchecked")
			KeyValuePair<String, Object> kv = (KeyValuePair<String, Object>) o;
			map.put(kv.getKey(), kv.getValue());
		});

		return map;
	}

	private CompletableFuture<Collection<Object>> getCollection(CollectionRef type, String key) {

		return get0(key).thenCompose(v -> {

			if (v != null) {
				if (v instanceof CollectionRef && ((CollectionRef) v).equals(type)) {
					return this.multimapCache.get(key);
				} else {
					throw new RuntimeException("The entry with the key: " + key + " is not a " + type.toString());
				}
			} else {
				return CompletableFuture.completedFuture(null);
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> exists(String key) {
		return exists0(key);
	}

	private CompletableFuture<Boolean> exists0(String key) {
		return cache.containsKeyAsync(key);
	}

	@BlockerTodo
	@Override
	public CompletableFuture<Object> get(String key) {
		return get0(key).thenApply(r -> {
			if (r != null && r instanceof CollectionRef) {
				return getCollection((CollectionRef) r, key).thenApply(c -> {
					return r.equals(CollectionRef.MAP) ? asMap(c) : c;
				});
			}
			return r;
		});
	}

	private CompletableFuture<Object> get0(String key) {
		return this.cache.getAsync(key);
	}

	@Override
	public CompletableFuture<Object> set(String key, Object value) {
		return set0(key, value);
	}

	private CompletableFuture<Object> set0(String key, Object value) {
		return this.cache.putAsync(key, value);
	}

	@Override
	public CompletableFuture<Object> setex(String key, Object value, Long lifespan) {
		return this.cache.putAsync(key, value, lifespan, TimeUnit.SECONDS);
	}

	@Override
	public CompletableFuture<Object> set(String key, Object value, Long maxIdle) {

		return this.cache.getAdvancedCache().getCacheEntryAsync(key).thenApply(r -> {

			Metadata.Builder builder = r != null ? r.getMetadata().builder() : new EmbeddedMetadata.Builder();
			Metadata meta = builder.maxIdle(maxIdle, TimeUnit.SECONDS).build();

			if (r != null) {
				r.setMetadata(meta);
				r.setValue(value);
			} else {
				this.cache.getAdvancedCache().put(key, value, meta);
			}

			return value;
		});
	}

	@Override
	public CompletableFuture<Integer> del(String key) {
		return del(new String[] { key });
	}

	@BlockerTodo()
	@Override
	public CompletableFuture<Integer> del(String... keys) {
		CompletableFuture<?> futures[] = new CompletableFuture[keys.length];

		for (int i = 0; i < keys.length; i++) {
			futures[i] = del0(keys[i]);
		}

		return CompletableFuture.allOf(futures).thenApply(r -> keys.length);
	}

	private CompletableFuture<Object> del0(String key) {
		return get0(key).thenApply(r -> {

			if (r != null && r instanceof CollectionRef) {
				this.multimapCache.remove(key);
			}

			return this.cache.removeAsync(key);
		});
	}

	@Override
	public CompletableFuture<Integer> incrby(String key, Integer amount) {

		return get0(key).thenApply(r -> {

			if (r == null || r instanceof Integer) {
				Integer i = r == null ? 0 : (Integer) r;

				i += amount;

				AdvancedCache<String, Object> cache = this.cache.getAdvancedCache();

				if (r != null) {
					cache.put(key, i, cache.getCacheEntry(key).getMetadata());
				} else {
					cache.put(key, i);
				}

				return i;

			} else {
				Exceptions.throwRuntime("The initial value for key: " + key + " must be an integer");
				return null;
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> expireInSecs(String key, Long lifespan) {
		return expireOrInvalidate(key, lifespan, null);
	}

	private CompletableFuture<Boolean> expireOrInvalidate(String key, Long lifespan, Long maxIdle) {

		return this.cache.getAdvancedCache().getCacheEntryAsync(key).thenCompose(entry -> {

			if (entry == null) {
				return CompletableFuture.completedFuture(false);
			}

			EmbeddedMetadata.Builder builder = new EmbeddedMetadata.Builder();

			if (lifespan != null) {
				builder.lifespan(lifespan, TimeUnit.SECONDS);
			}

			if (maxIdle != null) {
				builder.maxIdle(maxIdle, TimeUnit.SECONDS);
			}

			Metadata metadata = builder.build();
			entry.setMetadata(metadata);

			if (entry.getValue() instanceof CollectionRef) {
				return this.multimapCache.getEntry(key).thenAccept(e -> {
					e.get().setMetadata(metadata);
				}).thenApply(r -> true);
			}

			return CompletableFuture.completedFuture(true);
		});
	}

	@Override
	public CompletableFuture<Boolean> invalidateInSecs(String key, Long maxIdle) {
		return expireOrInvalidate(key, null, maxIdle);
	}

	@Override
	public CompletableFuture<Integer> slength(String key) {
		return getCollection(CollectionRef.SET, key).thenApply(c -> {
			if (c == null) {
				return null;
			}

			return c.size();
		});
	}

	@Override
	public CompletableFuture<Integer> sadd(String key, List<Object> elements) {
		return getCollection(CollectionRef.SET, key).thenApply(c -> {

			Integer newItems = 0;

			if (c != null) {

				for (Object e : elements) {
					if (!c.contains(e)) {
						c.add(e);
						newItems += 1;
					}
				}

				return newItems;

			} else {
				set0(key, CollectionRef.SET);
				this.multimapCache.put(key, elements);
				return elements.size();
			}
		});
	}

	@Override
	public CompletableFuture<Integer> sdel(String key, Object value) {
		return sdel(key, new Object[] { value });
	}

	@Override
	public CompletableFuture<Integer> sdel(String key, Object... values) {

		return getCollection(CollectionRef.SET, key).thenApply(c -> {

			if (c == null) {
				return 0;
			}

			Integer count = 0;
			List<Object> itemsToRemove = Arrays.asList(values);

			Iterator<Object> it = c.iterator();

			while (it.hasNext()) {
				Object o = it.next();
				if (itemsToRemove.contains(o)) {
					it.remove();
					count++;
				}
			}

			return count;
		});
	}

	@Override
	public CompletableFuture<Void> sget(String key, Function<Object, CompletableFuture<Void>> consumer) {
		return getCollection(CollectionRef.SET, key).thenCompose(c -> {

			if (c == null) {
				return CompletableFuture.completedFuture(null);
			}

			List<CompletableFuture<?>> futures = new ArrayList<>(c.size());

			c.forEach(o -> {
				futures.add(consumer.apply(o));
			});

			return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		});
	}

	@Override
	public CompletableFuture<Object> hset(String key, String field, Object value) {
		return getCollection(CollectionRef.MAP, key).thenApply(c -> {

			KeyValuePair<String, Object> kv = new KeyValuePair<String, Object>(field, value);

			if (c != null) {
				c.add(kv);
			} else {
				set0(key, CollectionRef.MAP);
				this.multimapCache.put(key, kv);
			}

			return value;
		});
	}

	@Override
	public CompletableFuture<Object> hget(String key, String field) {
		return getCollection(CollectionRef.MAP, key).thenApply(c -> {

			if (c != null) {
				Iterator<Object> it = c.iterator();

				while (it.hasNext()) {
					@SuppressWarnings("unchecked")
					KeyValuePair<String, Object> kv = (KeyValuePair<String, Object>) it.next();

					if (kv.getKey().equals(field)) {
						return kv.getValue();
					}
				}
			}
			return null;
		});
	}

	@Override
	public CompletableFuture<Map<String, Object>> hgetall(String key) {

		return getCollection(CollectionRef.MAP, key).thenApply(c -> {
			return c != null ? asMap(c) : null;
		});
	}

	@Override
	public CompletableFuture<Integer> hdel(String key, String field) {
		return hdel(key, new String[] { field });
	}

	@Override
	public CompletableFuture<Integer> hdel(String key, String... fields) {
		return getCollection(CollectionRef.MAP, key).thenApply(c -> {

			if (c == null) {
				return 0;
			}

			Integer count = 0;
			List<String> itemsToRemove = Arrays.asList(fields);

			Iterator<Object> it = c.iterator();

			while (it.hasNext()) {
				@SuppressWarnings("unchecked")
				KeyValuePair<String, Object> kv = (KeyValuePair<String, Object>) it.next();

				if (itemsToRemove.contains(kv.getKey())) {
					it.remove();
					count += 1;
				}
			}

			return count;
		});
	}

	@Override
	public CompletableFuture<Void> hkeys(String key, Function<String, CompletableFuture<Void>> consumer) {

		return getCollection(CollectionRef.MAP, key).thenCompose(c -> {

			if (c == null) {
				return CompletableFuture.completedFuture(null);
			}

			List<CompletableFuture<?>> futures = new ArrayList<>(c.size());

			c.forEach(o -> {
				@SuppressWarnings("unchecked")
				KeyValuePair<String, Object> kv = (KeyValuePair<String, Object>) o;
				futures.add(consumer.apply(kv.getKey()));
			});

			return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		});
	}

	@Override
	public CompletableFuture<Integer> hincrby(String key, String field, Integer amount) {
		return getCollection(CollectionRef.MAP, key).thenApply(c -> {

			if (c == null) {
				return null;
			}

			Iterator<Object> it = c.iterator();
			KeyValuePair<String, Object> entry = null;

			while (it.hasNext()) {
				@SuppressWarnings("unchecked")
				KeyValuePair<String, Object> kv = (KeyValuePair<String, Object>) it.next();
				if (kv.getKey().equals(field)) {

					if (!(kv.getValue() instanceof Number)) {
						Exceptions.throwRuntime("The value for hash field: " + field + " must be a number");
					}

					entry = kv;
					it.remove();
					break;
				}
			}

			if (entry == null) {
				entry = new KeyValuePair<>(field, 0);
			}

			Integer newValue = ((Integer) entry.getValue()) + amount;

			entry.setValue(newValue);

			c.add(entry);

			return newValue;
		});
	}

	@Override
	public CompletableFuture<Integer> hlen(String key) {
		return getCollection(CollectionRef.MAP, key).thenApply(c -> {
			if (c == null) {
				return null;
			}

			return c.size();
		});
	}

	@Override
	public CompletableFuture<CacheEntryType> type(String key) {
		return get0(key).thenApply(r -> {
			if (r.equals(CollectionRef.MAP)) {
				return CacheEntryType.HASH;
			} else if (r.equals(CollectionRef.SET)) {
				return CacheEntryType.SET;
			} else {
				return CacheEntryType.PRIMITIVE;
			}
		});
	}

}