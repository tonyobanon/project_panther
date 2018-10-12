package com.re.paas.api.spi;

import java.util.List;
import java.util.Map;

import com.re.paas.api.annotations.PlatformInternal;
import com.re.paas.api.designpatterns.Singleton;

@PlatformInternal
public interface SpiLocatorHandler {
	
	public static SpiLocatorHandler get() {
		return Singleton.get(SpiLocatorHandler.class);
	}
	
	default void start(String appId) {
		start(appId, SpiTypes.values());
	}

	default void start(SpiTypes[] types) {
		start(null, types);
	}

	public void start(String appId, SpiTypes[] types);
	
	public void reshuffleClasses();
	
	public Map<SpiTypes, BaseSPILocator> getDefaultLocators();

	public Map<SpiTypes, Map<String, List<Class<?>>>> getSpiClasses();

	public Map<SpiTypes, Boolean> getScanResult();

}
