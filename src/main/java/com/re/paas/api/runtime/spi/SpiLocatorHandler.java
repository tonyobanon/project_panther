package com.re.paas.api.runtime.spi;

import com.re.paas.api.annotations.ProtectionContext;
import com.re.paas.api.designpatterns.Singleton;

@ProtectionContext
public interface SpiLocatorHandler {
	
	public static SpiLocatorHandler get() {
		return Singleton.get(SpiLocatorHandler.class);
	}

	public void exists(SpiType type, Class<?> clazz);
}
