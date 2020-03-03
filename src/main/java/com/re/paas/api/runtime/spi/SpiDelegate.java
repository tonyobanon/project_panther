package com.re.paas.api.runtime.spi;

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.classes.AsyncDistributedMap;
import com.re.paas.api.classes.KeyValuePair;
import com.re.paas.api.classes.ParameterizedClass;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.ClassLoaders;
import com.re.paas.api.runtime.ConcreteIntrinsic;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.api.runtime.SecureMethod.CustomValidatorContext;
import com.re.paas.api.runtime.SecureMethod.Factor;
import com.re.paas.api.runtime.SecureMethod.IdentityStrategy;
import com.re.paas.api.runtime.SecureMethod.Validator;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.api.utils.Utils;

public abstract class SpiDelegate<T> {

	// private static final Logger LOG = Logger.get(SpiDelegate.class);

	private KeyValuePair<SpiType, Class<?>> type = null;

	protected static final String EXT_RESOURCE_PREFIX = "ext_";

	protected final Map<Object, Object> getLocalStore() {
		return getResourceSet(getSpiType()).getLocalStore();
	}

	/**
	 * Note: the distributed map returned cannot be modified using it's iterator
	 * @param name
	 * @return
	 */
	protected final Map<String, ?> getDistributedStore(String name) {
		return getResourceSet(getSpiType()).getDistributedStores().get(name);
	}

	private static final DelegateResorceSet getResourceSet(SpiType type) {
		return SpiDelegateHandler.get().getResources(type);
	}

	public final Class<?> getLocatorClassType() {

		ParameterizedClass c = ClassUtils.getParameterizedClass(getClass().getClassLoader(),
				getClass().getSuperclass().getGenericSuperclass());

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

	protected abstract Collection<?> getResourceObjects();

	@BlockerTodo("Most delegates should return this in their init(..) functions")
	
	protected final DelegateInitResult forEach(Function<Class<T>, ResourceStatus> consumer) {
		return SpiDelegateHandler.get().forEach(getSpiType(), consumer);
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
	public void shutdown() {
	}

	/**
	 * Release resources used by this delegate
	 */
	@SecureMethod(factor = Factor.CALLER, allowed = {
			SpiBase.class }, identityStrategy = IdentityStrategy.SINGLETON, restrictHierarchyAccess = true)
	public final void release() {
		
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
	public final void add0(List<Class<?>> classes) {

		add(Utils.toGenericList((classes)));

	}

	@SecureMethod(factor = Factor.CALLER, allowed = {
			SpiBase.class }, identityStrategy = IdentityStrategy.SINGLETON, restrictHierarchyAccess = true)

	public final List<Class<T>> remove0(List<Class<?>> classes) {

		return remove(Utils.toGenericList(classes));

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
		return ResourceStatus.UPDATED;
	}

	protected ResourceStatus remove(Class<T> clazz) {
		return ResourceStatus.UPDATED;
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
