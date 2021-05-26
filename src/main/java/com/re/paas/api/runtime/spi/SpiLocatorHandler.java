package com.re.paas.api.runtime.spi;

import com.re.paas.api.Singleton;
import com.re.paas.api.runtime.SecureMethod;

public interface SpiLocatorHandler {

	static SpiLocatorHandler get() {
		return Singleton.get(SpiLocatorHandler.class);
	}

	@SecureMethod
	Boolean exists(SpiType type, Class<?> clazz);
}
