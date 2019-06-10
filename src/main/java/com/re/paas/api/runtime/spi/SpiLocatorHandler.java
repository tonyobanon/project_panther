package com.re.paas.api.runtime.spi;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.MethodMeta;


public interface SpiLocatorHandler {
	
	public static SpiLocatorHandler get() {
		return Singleton.get(SpiLocatorHandler.class);
	}

	@MethodMeta
	public Boolean exists(SpiType type, Class<?> clazz);
}
