package com.re.paas.internal.classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import org.infinispan.multimap.api.embedded.MultimapCache;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.AsyncDistributedMap;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.utils.Collections;

@BlockerTodo("Don't we need to somehow synchronize on <multimap>")
@BlockerTodo("The key/value/entry view of this map are always buffered in memory. We need to build a custom iterator, since the entries could be large.")
@BlockerTodo("Add support for fully distributed entries, such that if an entry is a map itself, explode the key and store as a high-level entry")

public class MultimapBackedMap implements AsyncDistributedMap<String, Object> {

	private static final String KEYS = "$";

	private final MultimapCache<String, Object> multimap;
	private final Boolean alwaysReturnValue;

	public MultimapBackedMap(MultimapCache<String, Object> multimap, Boolean alwaysReturnValue) {
		this.multimap = multimap;
		this.alwaysReturnValue = alwaysReturnValue;
	}

	@Override
	public CompletableFuture<Integer> size() {
		return multimap.size().thenApply(l -> l.intValue());
	}

	@Override
	public CompletableFuture<Boolean> isEmpty() {
		return size().thenApply(s -> s > 0);
	}

	@Override
	public CompletableFuture<Boolean> containsKey(Object key) {
		return multimap.containsKey((String) key);
	}

	@Override
	public CompletableFuture<Boolean> containsValue(Object value) {
		return multimap.containsValue(value);
	}

	@Override
	public CompletableFuture<Object> get(Object key) {

		if (key.equals(KEYS)) {
			Exceptions.throwRuntime("Cannot use key: " + key);
		}

		return multimap.get((String) key).thenApply(col -> {

			if (col.isEmpty()) {
				return null;
			}

			return Collections.nthValue(col, 0);
		});
	}

	@Override
	public CompletableFuture<Object> put(String key, Object value) {

		if (key.equals(KEYS)) {
			Exceptions.throwRuntime("Cannot use key: " + key);
		}

		if (value == null) {
			Exceptions.throwRuntime("Value cannot be null");
		}

		ObjectWrapper<Object> oldValue = new ObjectWrapper<>();

		return containsKey(key).thenApply(contains -> {

			if (contains) {
				return remove0(key, false).thenApply(r -> oldValue.set(r));
			} else {
				return addKey(key);
			}
		}).thenAccept(r -> this.multimap.put(key, value)).thenApply(r -> oldValue.get());
	}

	private CompletableFuture<?> addKey(String key) {
		return multimap.put(KEYS, key);
	}

	private CompletableFuture<Boolean> removeKey(String key) {
		return multimap.remove(KEYS, key);
	}

	private CompletableFuture<Set<String>> getKeys(String key) {
		return multimap.get(KEYS).thenApply(col -> {
			return Collections.asSet(col);
		});
	}

	@Override
	public CompletableFuture<Object> remove(Object key) {

		if (key.equals(KEYS)) {
			Exceptions.throwRuntime("Cannot use key: " + key);
		}

		return remove0((String) key, true);
	}

	@Override
	public CompletableFuture<Boolean> remove(String key, Object value) {
		return remove0(key, true, v -> v.equals(value)).thenApply(v -> (Boolean) v);
	}

	private CompletableFuture<Object> remove0(String key, Boolean removeKey) {
		return this.remove0(key, removeKey, null);

	}

	private CompletableFuture<Object> remove0(String key, Boolean removeKey, Predicate<Object> predicate) {

		ObjectWrapper<Object> oldValue = new ObjectWrapper<>();

		CompletableFuture<Boolean> exists = (alwaysReturnValue || predicate != null)
				? multimap.get(key).thenApply(col -> {

					Object v = Collections.nthValue(col, 0);
					oldValue.set(v);

					if (predicate != null && !predicate.test(v)) {

						// Since this is used by: remove(String key, Object value), strategically set
						// <oldValue> to false and also return false, so this method returns immediately

						oldValue.set(false);
						return false;
					}

					return v != null;

				})
				: multimap.containsKey(key);

		return exists

				.thenApply(a -> {

					if (!a) {

						return oldValue.get();

					} else {

						return this.multimap.remove(key).thenApply(b -> {

							if (!b) {
								Exceptions.throwRuntime(
										"Error occured while remove key: " + key + " from " + this.multimap.toString());
							}

							if (removeKey) {
								return removeKey(key);
							} else {
								return CompletableFuture.completedFuture(null);
							}
						});
					}
				}).thenApply(r -> predicate != null ? true : oldValue.get());
	}

	@Override
	public CompletableFuture<?> putAll(Map<? extends String, ? extends Object> m) {

		List<CompletableFuture<?>> futures = new ArrayList<>(m.size());

		m.forEach((k, v) -> {
			futures.add(put(k, v));
		});

		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
	}

	@BlockerTodo("Test this: see comments below")
	@Override
	public CompletableFuture<?> clear() {

		return this.multimap.remove(v -> true);

//		Note: If the above does not work as expected, use this:
//	
//		return keySet().thenApply(keys -> {
//
//			List<CompletableFuture<?>> futures = new ArrayList<>(keys.size());
//
//			keys.forEach(k -> futures.add(remove0(k, false)));
//
//			return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
//
//		}).thenAccept(r -> remove0(KEYS, false));
	}

	@Override
	public CompletableFuture<Set<String>> keySet() {
		return getKeys(KEYS);
	}

	@Override
	public CompletableFuture<Collection<Object>> values() {

		ObjectWrapper<Collection<Object>> values = new ObjectWrapper<>();

		return keySet().thenApply(keys -> {

			List<CompletableFuture<?>> futures = new ArrayList<>(keys.size());
			values.set(new ArrayList<>(keys.size()));

			keys.forEach(k -> {
				futures.add(get(k).thenAccept(v -> values.get().add(v)));
			});

			return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		}).thenApply(r -> values.get());
	}

//	@Override
//	public <T> void forEach(Class<T> type, Consumer<T> consumer) {
//
//		ObjectWrapper<List<CompletableFuture<?>>> futures = new ObjectWrapper<>();
//
//		keySet().thenAccept(keys -> {
//
//			futures.set(new ArrayList<>(keys.size()));
//
//			keys.forEach(k -> {
//
//				CompletableFuture<?> future = get(k).thenAccept(v -> {
//					@SuppressWarnings("unchecked")
//					T value = (T) v;
//					consumer.accept(value);
//				});
//
//				futures.get().add(future);
//			});
//		});
//
//		CompletableFuture.allOf(futures.get().toArray(new CompletableFuture[futures.get().size()])).join();
//	}

	@Override
	public CompletableFuture<Set<Entry<String, Object>>> entrySet() {
		ObjectWrapper<Set<Entry<String, Object>>> values = new ObjectWrapper<>();

		return keySet().thenApply(keys -> {

			List<CompletableFuture<?>> futures = new ArrayList<>(keys.size());
			values.set(new HashSet<>(keys.size()));

			keys.forEach(k -> {
				futures.add(get(k).thenAccept(v -> values.get().add(Collections.asEntry(k, v))));
			});

			return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		}).thenApply(r -> values.get());
	}

	@Override
	public Map<String, Object> toSyncMap() {
		return new SyncDistributedMap<>(this);
	}

	public MultimapCache<String, ?> getMultimap() {
		return multimap;
	}
}
