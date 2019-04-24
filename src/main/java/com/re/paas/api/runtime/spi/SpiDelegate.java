package com.re.paas.api.runtime.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.re.paas.api.annotations.ProtectionContext;
import com.re.paas.api.annotations.ProtectionContext.Factor;
import com.re.paas.api.annotations.ProtectionContext.IdentityStrategy;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.annotations.develop.PlatformInternal;
import com.re.paas.api.classes.KeyValuePair;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.ExecutorFactory;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.api.utils.Utils;

/**
 * Note: T must be non-parameterized, since
 * ClassUtils.getGenericSuperclasses(..) does not yet support multi-nested
 * generic construct <br>
 * <br>
 * <b>Notes on Security</b>
 * <li>Delegates are responsible for enforcing code security within their
 * respective domains. Trusted delegates should ensure that the
 * {@link ExecutorFactory#execute(com.re.paas.api.app_provisioning.AppClassLoader, Boolean, Runnable)}
 * is used when running code from resource classes</li>
 */
@ProtectionContext
public abstract class SpiDelegate<T> {

	private static final Logger LOG = Logger.get(SpiDelegate.class);

	private KeyValuePair<SpiType, Class<?>> type = null;

	protected static final String DEFAULT_NAMESPACE_PREFIX = "default";
	protected static final String EXT_PREFIX = "ext";

	protected final <S> List<S> getList(Class<S> T, String namespace) {
		@SuppressWarnings("unchecked")
		List<S> o = (List<S>) get(namespace);
		return o;
	}

	protected final <S> void addToList(String namespace, S obj) {
		@SuppressWarnings("unchecked")
		List<S> e = (List<S>) get(namespace);
		if (e == null) {
			e = new ArrayList<S>();
			set(namespace, e);
		}
		if (!e.contains(obj)) {
			e.add(obj);
		} else {
			LOG.warn("List: " + this.getClass().getSimpleName() + "/" + namespace + " already contains element: "
					+ obj.toString() + ", skipping ..");
		}
	}

	protected final Map<Object, Object> getAll() {
		return getAll(type.getKey());
	}

	private static final Map<Object, Object> getAll(SpiType type) {
		return SpiDelegateHandler.get().getResources(type);
	}

	/**
	 * This filters the set of resources
	 * 
	 * @return
	 */
	protected final List<Class<?>> filter() {
		return null;
	}

	@SuppressWarnings("unchecked")
	protected final Object get(String namespace) {
		checkNamespaceAccess(namespace);
		Object o = getAll(type.getKey()).get(namespace);
		if (o instanceof Class) {
			return ClassUtils.createInstance((Class<T>) o);
		} else {
			return o;
		}
	}

	protected final boolean hasKey(String namespace) {
		checkNamespaceAccess(namespace);
		Object o = getAll(type.getKey()).get(namespace);
		return o != null;
	}

	protected final Object get() {
		return get(DEFAULT_NAMESPACE_PREFIX);
	}

	protected final void set(String namespace, Object obj) {
		checkNamespaceAccess(namespace);
		getAll(type.getKey()).put(namespace, obj);
	}

	protected final Object remove(String namespace) {
		checkNamespaceAccess(namespace);
		return getAll(type.getKey()).remove(namespace);
	}

	/**
	 * Since any thread can access {@link #getAll()}, we need to enforce some
	 * security constraints on threads
	 */
	@ProtectionContext(factor = Factor.CALLER, allowed = {
			AbstractResource.class }, identityStrategy = IdentityStrategy.ASSIGNABLE, allowInternal = false)
	private static final void checkNamespaceAccess(String namespace) {
		if (namespace.startsWith(EXT_PREFIX)) {

		}
	}

	public final boolean hasResourceKey(String namespace) {
		return hasKey(EXT_PREFIX + namespace);
	}

	public final Object getResource(String namespace) {
		return get(EXT_PREFIX + namespace);
	}

	public final void setResource(Object namespace, Object obj) {
		set(EXT_PREFIX + namespace, obj);
	}

	public final Object removeResource(String namespace) {
		return remove(EXT_PREFIX + namespace);
	}

	public final Class<?> getLocatorClassType() {
		return ClassUtils.getGenericRefs(getClass().getClassLoader(), getClass().getSuperclass().getGenericSuperclass())
				.get(0);
	}

	public final KeyValuePair<SpiType, Class<?>> getType() {
		return type;
	}

	@ProtectionContext(factor = Factor.CALLER, allowed = {
			SpiDelegateHandler.class }, identityStrategy = IdentityStrategy.SINGLETON, allowInternal = false)
	public final SpiDelegate<T> setType(KeyValuePair<SpiType, Class<?>> type) {
		this.type = type;
		return this;
	}

	public final SpiType getSpiType() {
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
	 * This function is used to initialize this delegate. Subclasses should ensure
	 * that this method takes no longer than 10 seconds to execute.
	 */
	@PlatformInternal
	public abstract DelegateInitResult init();

	/**
	 * This releases the resources used by this delegate, and performs any cleanup
	 * tasks. This is called before this delegate is taken out of service
	 */

	public void shutdown() {

	}

	/**
	 * This unloads all classes that have been previously loaded by this delegate
	 */
	@BlockerTodo("Make abstract, and implement across all delegates")
	@ProtectionContext
	public void unload() {
	}

	@ProtectionContext(factor = Factor.CALLER, allowed = {
			SpiDelegateHandler.class }, identityStrategy = IdentityStrategy.SINGLETON, allowInternal = false)
	public final void add0(List<Class<?>> classes) {
		add(Utils.toGenericList((classes)));
	}

	@ProtectionContext(factor = Factor.CALLER, allowed = {
			SpiBase.class }, identityStrategy = IdentityStrategy.SINGLETON, allowInternal = false)
	public final List<Class<T>> remove0(List<Class<?>> classes) {
		return remove(Utils.toGenericList(classes));
	}

	protected void add(List<Class<T>> classes) {
	}

	protected List<Class<T>> remove(List<Class<T>> classes) {
		return Collections.emptyList();
	}

	/**
	 * Any delegate that overrides this function as false, must have a dependency on
	 * {@link SpiType#CACHE_ADAPTER}
	 * 
	 * @return
	 */
	public boolean inMemory() {
		return true;
	}

}
