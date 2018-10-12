package com.re.paas.api.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.annotations.PlatformInternal;
import com.re.paas.api.classes.KeyValuePair;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.utils.ClassUtils;

/**
 * Note: T must be non-parameterized, since
 * ClassUtils.getGenericSuperclasses(..) does not yet support multi-nested
 * generic construct
 */
public abstract class SpiDelegate<T> {
	
	private static final Logger LOG = Logger.get(SpiDelegate.class);
	
	private KeyValuePair<SpiTypes, Class<?>> type = null;

	protected static final String DEFAULT_NAMESPACE = "default";
	
	public static Map<SpiTypes, Map<Object, Object>> getResources() {
		return SpiDelegateHandler.get().getResources();
	}

	public final void set(Object namespace, Object obj) {
		getAll(type.getKey()).put(namespace, obj);
	}

	public final <S> List<S> getList(Class<S> T, String namespace) {
		@SuppressWarnings("unchecked")
		List<S> o = (List<S>) get(namespace);
		return o;
	}

	public final <S> void addToList(String namespace, S obj) {
		@SuppressWarnings("unchecked")
		List<S> e = (List<S>) get(namespace);
		if (e == null) {
			e = new ArrayList<S>();
			set(namespace, e);
		}
		if (!e.contains(obj)) {
			e.add(obj);
		} else {
			LOG.warn("List: " + this.getClass().getSimpleName() + "/"
					+ namespace + " already contains element: " + obj.toString() + ", skipping ..");
		}
	}

	public Map<Object, Object> getAll() {
		return getAll(type.getKey());
	}

	protected static Map<Object, Object> getAll(SpiTypes type) {
		return SpiDelegateHandler.get().getResources().get(type);
	}

	public boolean hasKey(String namespace) {
		Object o = getAll(type.getKey()).get(namespace);
		return o != null;
	}

	@SuppressWarnings("unchecked")
	public Object get(String namespace) {
		Object o = getAll(type.getKey()).get(namespace);
		if (o instanceof Class) {
			return ClassUtils.createInstance((Class<T>) o);
		} else {
			return o;
		}
	}

	public Object get() {
		return get(DEFAULT_NAMESPACE);
	}

	public Object remove(String namespace) {
		return getAll(type.getKey()).remove(namespace);
	}
 
	public Class<?> getLocatorClassType() {
		return ClassUtils.getGenericSuperclasses(getClass()).get(0);
	}

	public KeyValuePair<SpiTypes, Class<?>> getType() {
		return type;
	}

	public SpiDelegate<T> setType(KeyValuePair<SpiTypes, Class<?>> type) {
		this.type = type;
		return this;
	}

	public final SpiTypes getSpiType() {
		return type.getKey();
	}
	
	protected final List<Class<T>> getResourceClasses() {
		List<Class<T>> result = new ArrayList<>();
		forEach(result::add);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	protected final void forEach(Consumer<Class<T>> consumer) {
		SpiDelegateHandler.get().forEach(getSpiType(), c -> {
			consumer.accept((Class<T>) c);
		});
	}

	/**
	 * This method is used to initialize the needed resources by this delegate
	 */
	@PlatformInternal
	public abstract void init();

	/**
	 * This releases the resources used by this delegate, and performs any cleanup
	 * tasks. This is called before this delegate is taken out of service
	 */

	@PlatformInternal
	public void destroy() {
		getAll().clear();
	}

	/**
	 * This unloads all classes that have been previously loaded by this delegate
	 */
	@BlockerTodo("Make abstract, and implement across all delegates")
	@PlatformInternal
	public void unload() {
	}

	@SuppressWarnings("unchecked")
	private final List<Class<T>> getClasses(List<Class<?>> classes) {
		List<Class<T>> o = new ArrayList<Class<T>>();
		classes.forEach(c -> {
			o.add((Class<T>) c);
		});
		return o;
	}

	@PlatformInternal
	public final void add0(List<Class<?>> classes) {
		add(getClasses(classes));
	}

	@PlatformInternal
	public final void remove0(List<Class<?>> classes) {
		remove(getClasses(classes));
	}

	protected void add(List<Class<T>> classes) {
	}

	protected void remove(List<Class<T>> classes) {
	}
	
	@PlatformInternal
	public boolean inMemory() {
		return true;
	}

}
