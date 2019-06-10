package com.re.paas.api.runtime.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Consumer;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.KeyValuePair;
import com.re.paas.api.classes.ParameterizedClass;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.MethodMeta;
import com.re.paas.api.runtime.MethodMeta.CustomValidatorContext;
import com.re.paas.api.runtime.MethodMeta.Factor;
import com.re.paas.api.runtime.MethodMeta.IdentityStrategy;
import com.re.paas.api.runtime.MethodMeta.Validator;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.api.utils.Utils;

public abstract class SpiDelegate<T> {

	private static final Logger LOG = Logger.get(SpiDelegate.class);

	private KeyValuePair<SpiType, Class<?>> type = null;

	protected static final String EXT_RESOURCE_PREFIX = "ext_";

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
		return getAll(getSpiType());
	}

	private static final Map<Object, Object> getAll(SpiType type) {
		return SpiDelegateHandler.get().getResources(type);
	}

	protected final Object get(String namespace) {
		Object o = getAll().get(namespace);
		if (o instanceof Class) {
			@SuppressWarnings("unchecked")
			Object r = ClassUtils.createInstance((Class<T>) o);
			return r;
		}
		return o;
	}

	protected final boolean hasKey(String namespace) {
		return getAll().containsKey(namespace);
	}

	protected final void set(String namespace, Object obj) {
		getAll().put(namespace, obj);
	}

	protected final Object remove(String namespace) {
		return getAll().remove(namespace);
	}

	@MethodMeta(factor = Factor.CALLER, allowed = {
			Resource.class }, identityStrategy = IdentityStrategy.ASSIGNABLE, onImplementation = true)
	@MethodMeta(validator = MetaFactoryValidators.ResourceValidator.class)

	public boolean hasResourceKey(String namespace) {
		return hasKey(EXT_RESOURCE_PREFIX + namespace);
	}

	@MethodMeta(factor = Factor.CALLER, allowed = {
			Resource.class }, identityStrategy = IdentityStrategy.ASSIGNABLE, onImplementation = true)
	@MethodMeta(validator = MetaFactoryValidators.ResourceValidator.class)

	public Object getResource(String namespace) {
		return get(EXT_RESOURCE_PREFIX + namespace);
	}

	@MethodMeta(factor = Factor.CALLER, allowed = {
			Resource.class }, identityStrategy = IdentityStrategy.ASSIGNABLE, onImplementation = true)
	@MethodMeta(validator = MetaFactoryValidators.ResourceValidator.class)

	public void setResource(Object namespace, Object obj) {
		set(EXT_RESOURCE_PREFIX + namespace, obj);
	}

	@MethodMeta(factor = Factor.CALLER, allowed = {
			Resource.class }, identityStrategy = IdentityStrategy.ASSIGNABLE, onImplementation = true)
	@MethodMeta(validator = MetaFactoryValidators.ResourceValidator.class)

	public Object removeResource(String namespace) {
		return remove(EXT_RESOURCE_PREFIX + namespace);
	}

	public final Class<?> getLocatorClassType() {
		ParameterizedClass c = ClassUtils.getParameterizedClass(getClass().getClassLoader(),
				getClass().getSuperclass().getGenericSuperclass());
		return c.getGenericTypes().get(0).getType();
	}

	public final KeyValuePair<SpiType, Class<?>> getType() {
		return type;
	}

	@MethodMeta(factor = Factor.CALLER, allowed = {
			SpiDelegateHandler.class }, identityStrategy = IdentityStrategy.SINGLETON, allowInternalAccess = false)
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

	protected final void forEach(Consumer<Class<T>> consumer) {
		SpiDelegateHandler.get().forEach(getSpiType(), c -> {
			@SuppressWarnings("unchecked")
			Class<T> clazz = (Class<T>) c;
			consumer.accept(clazz);
		});
	}

	/**
	 * This function is used to initialize this delegate. Subclasses should ensure
	 * that this method takes no longer than 10 seconds to execute.
	 */
	@MethodMeta
	public abstract DelegateInitResult init();

	public Boolean applies() {
		return true;
	}

	/**
	 * Indicate that delegate needs to be taken out of service
	 */
	@BlockerTodo("Make abstract, and implement across all delegates")
	public void shutdown() {}

	@MethodMeta(factor = Factor.CALLER, allowed = {
			SpiBase.class }, identityStrategy = IdentityStrategy.SINGLETON, allowInternalAccess = false)
	
	/**
	 * Release resources used by this delegate
	 */
	public final void release() {
		MetaFactoryValidators.ResourceValidator.remove(this);
		
		Map<Object, Object> rMap = getAll();
		if (!rMap.isEmpty()) {
			rMap.clear();
		}
	}

	@MethodMeta(factor = Factor.CALLER, allowed = {
			SpiDelegateHandler.class }, identityStrategy = IdentityStrategy.SINGLETON, allowInternalAccess = false)
	public final void add0(List<Class<?>> classes) {
		add(Utils.toGenericList((classes)));
	}

	@MethodMeta(factor = Factor.CALLER, allowed = {
			SpiBase.class }, identityStrategy = IdentityStrategy.SINGLETON, allowInternalAccess = false)

	public final List<Class<T>> remove0(List<Class<?>> classes) {
		return remove(Utils.toGenericList(classes));
	}
	
	
	@MethodMeta(factor = Factor.CALLER, allowed = {
			SpiBase.class }, identityStrategy = IdentityStrategy.SINGLETON, allowInternalAccess = false)
	
	public static final void emptyResourceValidatorCache(String appId) {
		MetaFactoryValidators.ResourceValidator.remove(appId);
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

	@BlockerTodo
	private static class MetaFactoryValidators {

		public static class ResourceValidator extends Validator {

			private static final Map<String, List<String>> classFrames = Collections.synchronizedMap(new HashMap<>());

			@Override
			public Boolean apply(CustomValidatorContext ctx) {

				Class<?> source = ctx.getSource().getDeclaringClass();
				Class<?> target = ctx.getTarget().getDeclaringClass();

				String appId = ClassUtils.getAppId(source);

				List<String> classFrames = ResourceValidator.classFrames.get(appId);

				if (classFrames == null) {
					classFrames = new ArrayList<>();
					ResourceValidator.classFrames.put(appId, classFrames);
				}

				String frame = source.getName() + "$" + ClassUtils.toString(target);

				if (classFrames.contains(frame)) {
					return true;
				}

				@SuppressWarnings("unchecked")
				Class<? extends SpiDelegate<?>> delegateClass = (Class<? extends SpiDelegate<?>>) ctx.getTarget()
						.getDeclaringClass();

				// Get direct super class, to get abstract delegate class
				@SuppressWarnings("unchecked")
				Class<? extends SpiDelegate<?>> abstractDelegateClass = (Class<? extends SpiDelegate<?>>) delegateClass
						.getSuperclass();

				SpiDelegate<?> delegate = Singleton.get(abstractDelegateClass);

				SpiType type = delegate.getSpiType();

				Boolean canAccess = SpiLocatorHandler.get().exists(type, source);

				if (canAccess) {
					classFrames.add(frame);
				}

				return canAccess;
			}

			private static void remove(String appId) {
				classFrames.remove(appId);
			}

			private static void remove(SpiDelegate<?> delegate) {

				String delegateClass = ClassUtils.toString(delegate.getClass());

				synchronized (classFrames) {
					for (List<String> classFrames : classFrames.values()) {
						ListIterator<String> it = classFrames.listIterator();
						while (it.hasNext()) {
							String s = it.next();
							if (s.endsWith("$" + delegateClass)) {
								it.remove();
							}
						}
					}
				}
			}
		}
	}
}
