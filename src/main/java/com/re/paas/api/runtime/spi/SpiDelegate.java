package com.re.paas.api.runtime.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.re.paas.api.Singleton;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.AsyncDistributedMap;
import com.re.paas.api.classes.KeyValuePair;
import com.re.paas.api.classes.ParameterizedClass;
import com.re.paas.api.runtime.ClassLoaders;
import com.re.paas.api.runtime.ConcreteIntrinsic;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.api.runtime.SecureMethod.CustomValidatorContext;
import com.re.paas.api.runtime.SecureMethod.Factor;
import com.re.paas.api.runtime.SecureMethod.IdentityStrategy;
import com.re.paas.api.runtime.SecureMethod.Validator;
import com.re.paas.api.utils.ClassUtils;

public abstract class SpiDelegate<T> {

	// private static final Logger LOG = Logger.get(SpiDelegate.class);

	private KeyValuePair<SpiType, Class<?>> type = null;

	protected static final String EXT_RESOURCE_PREFIX = "ext_";

	protected final Map<Object, Object> getLocalStore() {
		return getResourceSet(getSpiType()).getLocalStore();
	}

	/**
	 * Note: the distributed map returned cannot be modified using it's iterator
	 * 
	 * @param name
	 * @return
	 */
	protected final AsyncDistributedMap<String, ?> getDistributedStore(String name) {
		return getResourceSet(getSpiType()).getDistributedStores().get(name);
	}

	private static final DelegateResorceSet getResourceSet(SpiType type) {
		return SpiDelegateHandler.get().getResources(type);
	}

	public Class<?> getLocatorClassType() {

		Class<?> abstractSuperclass = ClassUtils.getSuperclass(getClass(), getClass().getSuperclass());
		
		assert abstractSuperclass.getSuperclass().equals(SpiDelegate.class);
		
		ParameterizedClass c = ClassUtils.getParameterizedClass(getClass().getClassLoader(),
				abstractSuperclass.getGenericSuperclass());

		return c.getGenericTypes().get(0).getType();
	}

	public final KeyValuePair<SpiType, Class<?>> getType() {
		return type;
	}

	@SecureMethod(factor = Factor.CALLER, allowed = {
			SpiDelegateHandler.class }, identityStrategy = IdentityStrategy.SINGLETON, restrictHierarchyAccess = true)
	public final SpiDelegate<T> setType(KeyValuePair<SpiType, Class<?>> type) {
		this.type = type;
		return this;
	}

	public final SpiType getSpiType() {
		return type.getKey();
	}

	protected final ResourcesInitResult addResources(Function<Class<T>, ResourceStatus> consumer) {
		return SpiDelegateHandler.get().addResources(getSpiType(), consumer);
	}

	/**
	 * This function is used to initialize this delegate. Subclasses should ensure
	 * that this method takes no longer than 10 seconds to execute.
	 */
	@SecureMethod
	public abstract DelegateInitResult init();

	public Boolean applies() {
		return true;
	}

	/**
	 * Indicate that delegate needs to be taken out of service
	 */
	@BlockerTodo("Make abstract, and implement across all delegates")
	public void shutdown(ShutdownPhase phase) {
	}

	/**
	 * Release resources used by this delegate
	 */
	@SecureMethod(factor = Factor.CALLER, allowed = {
			SpiBase.class }, identityStrategy = IdentityStrategy.SINGLETON, restrictHierarchyAccess = true)
	public final void release(ShutdownPhase phase) {
		
		shutdown(phase);

		MetaFactoryValidators.ResourceValidator.remove(this);
		SpiDelegateHandler.get().releaseResources(getSpiType());
	}

	@SecureMethod(factor = Factor.CALLER, allowed = {
			SpiDelegateHandler.class }, identityStrategy = IdentityStrategy.SINGLETON)
	public boolean requiresDistributedStore() {
		return false;
	}

	/**
	 * This could either be a string list representing the store names, or a list of
	 * {@link DistributedStoreConfig} objects
	 * 
	 * @return
	 */
	@SecureMethod(factor = Factor.CALLER, allowed = {
			SpiDelegateHandler.class }, identityStrategy = IdentityStrategy.SINGLETON)
	public List<Object> distributedStoreNames() {
		return Collections.emptyList();
	}

	@SecureMethod(factor = Factor.CALLER, allowed = {
			SpiDelegateHandler.class }, identityStrategy = IdentityStrategy.SINGLETON, restrictHierarchyAccess = true)
	public final Collection<Class<T>> add0(List<Class<?>> classes) {
		return this.processClasses(classes, this::add);
	}

	@SecureMethod(factor = Factor.CALLER, allowed = {
			SpiBase.class }, identityStrategy = IdentityStrategy.SINGLETON, restrictHierarchyAccess = true)
	public final Collection<Class<T>> remove0(List<Class<?>> classes) {
		return this.processClasses(classes, this::remove);
	}

	@SuppressWarnings("unchecked")
	private Collection<Class<T>> processClasses(List<Class<?>> classes, Function<Class<T>, ResourceStatus> function) {
		return classes.stream().map(c -> {
			return com.re.paas.api.utils.Collections.asEntry((Class<T>) c, function.apply((Class<T>) c));
		}).filter(e -> e.getValue() == ResourceStatus.ERROR).collect(Collectors
				.toMap(Map.Entry<Class<T>, ResourceStatus>::getKey, Map.Entry<Class<T>, ResourceStatus>::getValue))
				.keySet();
	}

	@SecureMethod(factor = Factor.CALLER, allowed = {
			SpiBase.class }, identityStrategy = IdentityStrategy.SINGLETON, restrictHierarchyAccess = true)

	public static final void emptyResourceValidatorCache(String appId) {
		MetaFactoryValidators.ResourceValidator.remove(appId);
	}

	@SecureMethod(factor = Factor.CALLER, allowed = {
			SpiDelegateHandler.class }, identityStrategy = IdentityStrategy.SINGLETON)
	public Comparator<Class<? extends T>> getClassComparator() {
		return null;
	}

	@SecureMethod(factor = Factor.CALLER, allowed = {
			SpiDelegateHandler.class }, identityStrategy = IdentityStrategy.SINGLETON)
	public final Boolean canRegisterInplace0(Class<?> clazz) {
		@SuppressWarnings("unchecked")
		Class<T> c = (Class<T>) clazz;
		return canRegisterInPlace(c);
	}

	protected Boolean canRegisterInPlace(Class<T> clazz) {
		return true;
	}

	protected ResourceStatus add(Class<T> clazz) {
		return ResourceStatus.NOT_UPDATED;
	}

	protected ResourceStatus remove(Class<T> clazz) {
		return ResourceStatus.NOT_UPDATED;
	}

	@ConcreteIntrinsic
	@SecureMethod(factor = Factor.CALLER, allowed = { Resource.class }, identityStrategy = IdentityStrategy.ASSIGNABLE)
	@SecureMethod(validator = MetaFactoryValidators.ResourceValidator.class)

	public Object get(String key) {
		return getLocalStore().get(EXT_RESOURCE_PREFIX + key);
	}

	@ConcreteIntrinsic
	@SecureMethod(factor = Factor.CALLER, allowed = { Resource.class }, identityStrategy = IdentityStrategy.ASSIGNABLE)
	@SecureMethod(validator = MetaFactoryValidators.ResourceValidator.class)

	public void set(Object key, Object value) {
		getLocalStore().put(EXT_RESOURCE_PREFIX + key, value);
	}

	@ConcreteIntrinsic
	@SecureMethod(factor = Factor.CALLER, allowed = { Resource.class }, identityStrategy = IdentityStrategy.ASSIGNABLE)
	@SecureMethod(validator = MetaFactoryValidators.ResourceValidator.class)

	public Object remove(String namespace) {
		return getLocalStore().remove(EXT_RESOURCE_PREFIX + namespace);
	}

	@BlockerTodo
	protected static final class MetaFactoryValidators {

		public static class ResourceValidator extends Validator {

			private static final Map<String, List<String>> classFrames = Collections.synchronizedMap(new HashMap<>());

			@Override
			public Boolean apply(CustomValidatorContext ctx) {

				Class<?> source = ctx.getSource().getDeclaringClass();
				Class<?> target = ctx.getTarget().getDeclaringClass();

				String appId = ClassLoaders.getId(source);

				List<String> classFrames = ResourceValidator.classFrames.get(appId);

				if (classFrames == null) {
					classFrames = new ArrayList<>();
					ResourceValidator.classFrames.put(appId, classFrames);
				}

				String frame = ClassUtils.asString(source) + "%" + ClassUtils.asString(target);

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

				String delegateClass = ClassUtils.asString(delegate.getClass());

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
