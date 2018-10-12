package com.re.paas.internal.spi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.re.paas.api.annotations.BlockerBlockerTodo;
import com.re.paas.api.annotations.PlatformInternal;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.app_provisioning.AppProvisioner;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.KeyValuePair;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.spi.SpiBase;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiDelegateHandler;
import com.re.paas.api.spi.SpiLocatorHandler;
import com.re.paas.api.spi.SpiTypes;
import com.re.paas.internal.errors.SpiError;

public class SpiBaseImpl implements SpiBase {

	@BlockerBlockerTodo("See comments")
	public void start(String appId) {
		
		if(appId.equals(AppProvisioner.get().defaultAppId())) {
			// Call Marketplace to validate apps for this installation
		}
		
		SpiLocatorHandler.get().start(appId);
		SpiDelegateHandler.get().start(appId);
		SpiLocatorHandler.get().reshuffleClasses();
	}

	public void stop() {
		
		Map<SpiTypes, SpiDelegate<?>> delegatesMap = SpiDelegateHandler.get().getDelegates();

		Iterator<SpiDelegate<?>> it = delegatesMap.values().iterator();
		int len = delegatesMap.size();

		ArrayList<SpiDelegate<?>> delegates = new ArrayList<>(len);

		// We need to invert the map, then call destroy
		for (int i = len - 1; i >= 0; i--) {
			delegates.add(i, it.next());
		}

		delegates.forEach(delegate -> {
			delegate.destroy();
		});
	}

	public void stop(String appId) {

		if (!canStop(appId)) {
			Exceptions.throwRuntime(PlatformException.get(SpiError.APPLICATION_IS_CURRENTLY_IN_USE, appId));
		}

		Logger.get().info("Stopping application: " + appId);
		stop0(appId);
	}

	public final String getLocatorConfigKey(SpiTypes type) {
		return "platform.spi." + type.toString().toLowerCase() + ".locator";
	}

	public final String getDelegateConfigKey(SpiTypes type) {
		return "platform.spi." + type.toString().toLowerCase() + ".delegate";
	}

	/**
	 * Behind the scenes, this method checks if this app has an spi delegate/locator
	 * that can be taken out of service and replaced with a similar delegate from
	 * another app
	 * 
	 * @param appId
	 * @return
	 */
	@PlatformInternal
	public boolean canStop(String appId) {

		AppClassLoader cl = AppProvisioner.get().getClassloader(appId);
		Map<SpiTypes, SpiDelegate<?>> delegates = SpiDelegateHandler.get().getDelegates();
		
		
		for (SpiTypes type : SpiTypes.values()) {

			if (SpiLocatorHandler.get().getDefaultLocators().get(type).getClass().getClassLoader().equals(cl)) {
				KeyValuePair<String, ClassLoader> config = AppProvisioner.get().getConfiguration(null,
						getLocatorConfigKey(type));
				if (config == null) {
					return false;
				}
			}

			if (delegates.get(type).getClass().getClassLoader().equals(cl)) {
				KeyValuePair<String, ClassLoader> config = AppProvisioner.get().getConfiguration(null,
						getDelegateConfigKey(type));
				if (config == null) {
					return false;
				}
			}
		}

		return true;
	}

	private static void stop0(String appId) {

		Map<SpiTypes, SpiDelegate<?>> delegates = SpiDelegateHandler.get().getDelegates();
		AppClassLoader cl = AppProvisioner.get().getClassloader(appId);

		SpiLocatorHandler.get().getSpiClasses().forEach((type, apps) -> {

			assert cl.isStopping();

			List<Class<?>> classes = apps.remove(appId);

			if (classes == null || classes.isEmpty()) {
				return;
			}

			// Get the corresponding delegate

			SpiDelegate<?> delegate = delegates.get(type);
			delegate.remove0(classes);

			if (SpiLocatorHandler.get().getDefaultLocators().get(type).getClass().getClassLoader().equals(cl)) {

				// Start new locator
				SpiLocatorHandler.get().start(new SpiTypes[] { type });
			}

			if (delegate.getClass().getClassLoader().equals(cl)) {
				// Start new delegate
				SpiDelegateHandler.get().start(new SpiTypes[] { type });
			}
		});

	}
	
}
