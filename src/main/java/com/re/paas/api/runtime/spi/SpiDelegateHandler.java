package com.re.paas.api.runtime.spi;

import java.util.Map;
import java.util.function.Consumer;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.internal.runtime.security.Secure;
import com.re.paas.internal.runtime.security.Secure.Factor;
import com.re.paas.internal.runtime.security.Secure.IdentityStrategy;


public interface SpiDelegateHandler {

	public static SpiDelegateHandler get() {
		return Singleton.get(SpiDelegateHandler.class);
	}

	@Secure
	Map<SpiType, SpiDelegate<?>> getDelegates();

	
	@Secure(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SAME, allowed = {
			SpiDelegate.class })
	@Secure(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SINGLETON, allowed = {
			SpiBase.class })
	Map<Object, Object> getResources(SpiType type);

	
	@Secure(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SAME, allowed = {
			SpiDelegate.class })
	void forEach(SpiType type, Consumer<Class<?>> consumer);

}
