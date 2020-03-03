package com.re.paas.integrated.models;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

import com.github.zafarkhaja.semver.Version;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.models.AbstractModelDelegate;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.Model;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.core.keys.ConfigKeys;

public class ModelDelegate extends AbstractModelDelegate {

	public static final String DEFAULT_MODEL_VERSION = "";

	private static LinkedHashMap<String, BaseModel> linkedModels = new LinkedHashMap<String, BaseModel>();

	@Override
	public DelegateInitResult init() {
		Consumer<Class<BaseModel>> consumer = (c) -> {
			addConcreteModel(null, c);
		};
		return forEach(consumer);
	}

	@Override
	protected void add(List<Class<BaseModel>> classes) {
		classes.forEach(c -> {
			addConcreteModel(null, c);
		});
	}

	@Override
	protected List<Class<BaseModel>> remove(List<Class<BaseModel>> classes) {
		classes.forEach(c -> {
			BaseModel instance = com.re.paas.internal.classes.ClassUtil.createInstance(c);
			linkedModels.remove(instance.path());
		});
		return classes;
	}

	public Collection<BaseModel> getModels() {
		return linkedModels.values();
	}

	private static final void addConcreteModel(String dependants, Class<? extends BaseModel> model) {

		// Logger.info("Registering " + model.getSimpleName());

		Model modelAnnotation = model.getAnnotation(Model.class);

		if (modelAnnotation != null) {

			if (!modelAnnotation.enabled()) {
				return;
			}

			for (Class<? extends BaseModel> dependency : modelAnnotation.dependencies()) {

				if (dependants != null) {
					if (dependants.contains(dependency.getSimpleName())) {
						// Circular reference was detected
						Exceptions.throwRuntime(new RuntimeException("Circular reference was detected: "
								+ (dependants + " -> " + model.getSimpleName() + " -> " + dependency.getSimpleName())
										.replaceAll(dependency.getSimpleName(),
												"(" + dependency.getSimpleName() + ")")));
					}

					addConcreteModel(dependants + " -> " + model.getSimpleName(), dependency);

				} else {
					addConcreteModel(model.getSimpleName(), dependency);
				}
			}
		}

		BaseModel instance = com.re.paas.internal.classes.ClassUtil.createInstance(model);

		if (!linkedModels.containsKey(instance.path())) {
			linkedModels.put(instance.path(), instance);
		}
	}

	/**
	 * 
	 * @TRodo: Model.preInstall() should not be responsible for calling start(..),
	 *         please fix
	 */
	@BlockerTodo("Call this inside init()")
	@BlockerTodo("The function CANNOT use ConfigModel to store versioning info, we need to store in local config")
	private void startAll() {

		if (!PlatformModel.isInstalled()) {

			Logger.get().debug("Preinstalling Models");

			// Pre-install models

			getModels().forEach(e -> {
				Logger.get().debug("Pre-installing " + e.getClass().getSimpleName());
				try {
					e.preInstall();
				} catch (Exception ex) {
					Logger.get().fatal("An error occurred while preinstalling " + e.getClass().getName());
					Exceptions.throwRuntime(ex);
				}
			});

			ConfigModel.putString(ConfigKeys.CURRENT_PLATFORM_VERSION, "0.0.1-rc.1+build.1");

		} else {

			Version platformVersion = Version.valueOf(ConfigModel.get(ConfigKeys.CURRENT_PLATFORM_VERSION));
			ObjectWrapper<Version> newPlatformVersion = new ObjectWrapper<Version>().set(platformVersion);

			// Check for model updates
			getModels().forEach(e -> {

				Class<?> c = e.getClass();

				// Silently ignore
				if (!c.isAnnotationPresent(Model.class)) {
					return;
				}

				Model classAnnotation = c.getAnnotation(Model.class);
				if (classAnnotation.version().equals(DEFAULT_MODEL_VERSION)) {
					return;
				}

				Version modelVersion = Version.valueOf(classAnnotation.version());

				if (modelVersion.greaterThan(platformVersion)) {

					Logger.get().debug("Updating " + c.getSimpleName());

					try {
						e.update();
					} catch (Exception ex) {
						Logger.get().fatal("An error occurred while updating " + e.getClass().getName());
						Exceptions.throwRuntime(ex);
					}

					if (modelVersion.greaterThan(newPlatformVersion.get())) {
						newPlatformVersion.set(modelVersion);
					}

				}

			});

			if (newPlatformVersion.get().greaterThan(platformVersion)) {

				Logger.get().debug("Updating platform version to: " + newPlatformVersion.get().toString());
				ConfigModel.putString(ConfigKeys.CURRENT_PLATFORM_VERSION, newPlatformVersion.get().toString());
			}

			// Start models

			getModels().forEach(e -> {

				Logger.get().debug("Starting " + e.getClass().getSimpleName());
				try {
					e.start();
				} catch (Exception ex) {
					Logger.get().fatal("An error occurred while starting " + e.getClass().getName());
					Exceptions.throwRuntime(ex);
				}

			});
		}
	}

}
