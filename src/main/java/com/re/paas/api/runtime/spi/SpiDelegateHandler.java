package com.re.paas.api.runtime.spi;

import java.util.Map;
import java.util.function.Consumer;

import com.re.paas.api.annotations.ProtectionContext;
import com.re.paas.api.annotations.ProtectionContext.Factor;
import com.re.paas.api.annotations.ProtectionContext.IdentityStrategy;
import com.re.paas.api.designpatterns.Singleton;

@ProtectionContext
public interface SpiDelegateHandler {

	public static SpiDelegateHandler get() {
		return Singleton.get(SpiDelegateHandler.class);
	}

	Map<SpiType, SpiDelegate<?>> getDelegates();

	@ProtectionContext(factor = Factor.CALLER, identityStrategy = IdentityStrategy.ASSIGNABLE, allowed = {
			SpiDelegate.class })
	Map<Object, Object> getResources(SpiType type);

	@ProtectionContext(factor = Factor.CALLER, identityStrategy = IdentityStrategy.ASSIGNABLE, allowed = {
			SpiDelegate.class })
	void forEach(SpiType type, Consumer<Class<?>> consumer);

}
