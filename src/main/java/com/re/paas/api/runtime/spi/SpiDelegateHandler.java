package com.re.paas.api.runtime.spi;

import java.util.Map;
import java.util.function.Consumer;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.MethodMeta;
import com.re.paas.api.runtime.MethodMeta.Factor;
import com.re.paas.api.runtime.MethodMeta.IdentityStrategy;


public interface SpiDelegateHandler {

	public static SpiDelegateHandler get() {
		return Singleton.get(SpiDelegateHandler.class);
	}

	@MethodMeta(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SINGLETON, allowed = {
			SpiBase.class })
	@MethodMeta(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SINGLETON, allowed = {
			SpiLocatorHandler.class })
	Map<SpiType, SpiDelegate<?>> getDelegates();

	
	@MethodMeta(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SAME, allowed = {
			SpiDelegate.class })
	@MethodMeta(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SINGLETON, allowed = {
			SpiBase.class })
	Map<Object, Object> getResources(SpiType type);

	
	@MethodMeta(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SAME, allowed = {
			SpiDelegate.class })
	void forEach(SpiType type, Consumer<Class<?>> consumer);

}
