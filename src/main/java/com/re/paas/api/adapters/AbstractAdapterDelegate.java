package com.re.paas.api.adapters;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.re.paas.api.Adapter;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.api.runtime.SecureMethod.Factor;
import com.re.paas.api.runtime.SecureMethod.IdentityStrategy;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.ResourceStatus;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.api.utils.Collections;
import com.re.paas.internal.fusion.services.SystemAdapterService;

public abstract class AbstractAdapterDelegate<U extends Object, T extends Adapter<U>> extends SpiDelegate<T> {

	private AdapterConfig config;
	private static final String ADAPTERS_RESOURCE_NAMESPACE = "ADAPTERS_RESOURCE_NAMESPACE";

	public final DelegateInitResult init() {

		createResourceMaps();

		this.addResources(this::add0);

		// At least one adapter resource must have been discovered
		if (getAdapters().isEmpty()) {
			return DelegateInitResult.FAILURE.setErrorMessage("There was no adapter found");
		}

		AdapterType type = Collections.nthValue(getAdapters(), 0).getType();

		AdapterConfig config = new AdapterConfig(type);

		// Load config file
		if (config.load() != null) {

			// Set configuration
			setConfig(config);

			// Load adapter resource
			this.load(LoadPhase.START);

			return DelegateInitResult.SUCCESS;

		} else {
			return DelegateInitResult.PENDING_ADAPTER_CONFIGURATION.setAdapterType(type);
		}
	}

	protected Collection<?> getResourceObjects() {
		return getAdapters().values();
	}

	public AdapterConfig getConfig() {
		return config;
	}

	@SecureMethod
	public AbstractAdapterDelegate<U, T> setConfig(AdapterConfig config) {
		this.config = config;
		return this;
	}

	@SecureMethod
	public abstract Boolean load(LoadPhase phase);

	public T getAdapter(String name) {
		return getAdapters().get(name);
	}

	public boolean requiresMigration() {
		return false;
	}

	/**
	 * 
	 * @param outgoing The outgoing adapter
	 */
	public void migrate(U outgoing, BiConsumer<Integer, String> listener) {
	}

	public T getAdapter() {
		return getAdapters().get(getConfig().getAdapterName());
	}

	@Override
	protected final ResourceStatus add(Class<T> clazz) {
		return this.add0(clazz);
	}

	private ResourceStatus add0(Class<T> c) {

		Map<String, T> adapters = getAdapters();

		T o = com.re.paas.internal.classes.ClassUtil.createInstance(c);
		if (adapters.containsKey(o.name())) {
			return ResourceStatus.ERROR.setMessage("An adapter already exists with the name: " + o.name());
		}

		adapters.put(o.name(), o);
		return ResourceStatus.UPDATED;
	}

	@Override
	protected final ResourceStatus remove(Class<T> c) {
		Map<String, T> adapters = getAdapters();

		if (ClassUtils.equals(c, getAdapter().getClass())) {
			return ResourceStatus.ERROR
					.setMessage("Adapter: " + ClassUtils.asString(getAdapter().getClass()) + " is still active");
		}

		T o = com.re.paas.internal.classes.ClassUtil.createInstance(c);
		adapters.remove(o.name());

		return ResourceStatus.UPDATED;
	}

	private void createResourceMaps() {
		getLocalStore().put(ADAPTERS_RESOURCE_NAMESPACE, new HashMap<>());
	}

	@SecureMethod(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SAME, allowed = {
			SystemAdapterService.class })
	public Map<String, T> getAdapters() {
		@SuppressWarnings("unchecked")
		Map<String, T> o = (Map<String, T>) getLocalStore().get(ADAPTERS_RESOURCE_NAMESPACE);
		return o;
	}

	static {
	}

}
