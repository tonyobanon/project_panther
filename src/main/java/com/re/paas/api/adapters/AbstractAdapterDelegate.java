package com.re.paas.api.adapters;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.re.paas.api.Adapter;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.spi.DelegateInitResult;
import com.re.paas.api.spi.DelegateSpec;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;
import com.re.paas.api.threadsecurity.ThreadSecurity;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.Platform;

@DelegateSpec(dependencies = { SpiTypes.NODE_ROLE })
public abstract class AbstractAdapterDelegate<T extends Adapter> extends SpiDelegate<T> {

	private AdapterConfig config;
	private static final String ADAPTERS_RESOURCE_NAMESPACE = "ADAPTERS_RESOURCE_NAMESPACE";

	public final DelegateInitResult init() {

		createResourceMaps();

		add(getResourceClasses());

		// At least one adapter resource must have been discovered
		if (getResourceClasses().isEmpty()) {
			return DelegateInitResult.FAILURE.setError("There was no adapter found");
		}

		// If this is the master, load adapter if configuration is available
		if (Platform.isInstalled() && NodeRole.getDelegate().isMaster()) {

			AdapterType type = ClassUtils.createInstance((Class<? extends Adapter>) getResourceClasses().get(0))
					.getType();

			AdapterConfig config = new AdapterConfig(type);

			try {

				// Load config file
				config.load();

				// Set configuration
				setConfig(config);

				// Load adapter resource
				this.load();

				return DelegateInitResult.SUCCESS;

			} catch (Exception e) {

				if (e instanceof FileNotFoundException) {
					return DelegateInitResult.PENDING_ADAPTER_CONFIGURATION.setType(type);
				}

				throw e;
			}
		}

		// * If platform is not installed, this config will be populated, and adapter
		// will be loaded, during the installation process

		// * If this is not a master, then the adapter configuration will be ingested
		// when this node joins the cluster

		return DelegateInitResult.SUCCESS;
	}

	public AdapterConfig getConfig() {
		return config;
	}

	public AbstractAdapterDelegate<T> setConfig(AdapterConfig config) {
		ThreadSecurity.verify();
		this.config = config;
		return this;
	}

	public abstract Object load();

	public T getAdapter(String name) {
		return getAdapters().get(name);
	}

	public T getAdapter() {
		if (getConfig() != null) {
			return getAdapters().get(getConfig().getAdapterName());
		}
		return null;
	}

	@Override
	protected final void add(List<Class<T>> classes) {

		Map<String, T> adapters = getAdapters();

		classes.forEach(c -> {
			T o = ClassUtils.createInstance(c);
			if (adapters.containsKey(o.name())) {
				Exceptions.throwRuntime("An adapter already exists with the name: " + o.name());
			}
			adapters.put(o.name(), o);
		});
	}

	@Override
	protected final void remove(List<Class<T>> classes) {

		Map<String, T> adapters = getAdapters();

		// Todo: Do not remove if active

		classes.forEach(c -> {
			T o = ClassUtils.createInstance(c);
			adapters.remove(o.name());
		});
	}

	private void createResourceMaps() {
		set(ADAPTERS_RESOURCE_NAMESPACE, new HashMap<>());
	}

	public Map<String, T> getAdapters() {
		@SuppressWarnings("unchecked")
		Map<String, T> o = (Map<String, T>) getAll().get(ADAPTERS_RESOURCE_NAMESPACE);
		return o;
	}

}
