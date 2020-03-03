package com.re.paas.api.runtime.spi;

import java.util.List;

import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.api.runtime.SecureMethod.Factor;
import com.re.paas.api.runtime.SecureMethod.IdentityStrategy;

public interface SpiLocatorHandler {

	static SpiLocatorHandler get() {
		return Singleton.get(SpiLocatorHandler.class);
	}

	@SecureMethod
	Boolean exists(SpiType type, Class<?> clazz);

	@SecureMethod(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SINGLETON, allowed = {
			AppClassLoader.class })
	void addDependencyPath(String source, List<String> targets);
}
