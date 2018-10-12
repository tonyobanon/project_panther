package com.re.paas.api.spi;

import java.util.Map;
import java.util.function.Consumer;

import com.re.paas.api.annotations.PlatformInternal;
import com.re.paas.api.designpatterns.Singleton;

@PlatformInternal
public interface SpiDelegateHandler {

	public static SpiDelegateHandler get() {
		return Singleton.get(SpiDelegateHandler.class);
	}
	
	default void start(String appId) {
		start(appId, SpiTypes.values());
	}

	default void start(SpiTypes[] types) {
		start(null, types);
	}

	default void start(String appId, SpiTypes[] types) {
		start(null, appId, types);
	}
	
	void start(String dependants, String appId, SpiTypes[] types);
	
	Map<SpiTypes, SpiDelegate<?>> getDelegates();
	
	Map<SpiTypes, Map<Object, Object>> getResources();
	
	@PlatformInternal
	public void forEach(SpiTypes type, Consumer<Class<?>> consumer);
	
	
}
