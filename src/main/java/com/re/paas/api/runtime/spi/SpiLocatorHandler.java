package com.re.paas.api.runtime.spi;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.internal.runtime.security.Secure;


public interface SpiLocatorHandler {
	
	public static SpiLocatorHandler get() {
		return Singleton.get(SpiLocatorHandler.class);
	}

	@Secure
	public void exists(SpiType type, Class<?> clazz);
}
