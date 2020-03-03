package com.re.paas.api.runtime.spi;

import java.util.Map;
import java.util.function.Function;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.api.runtime.SecureMethod.Factor;
import com.re.paas.api.runtime.SecureMethod.IdentityStrategy;


public interface SpiDelegateHandler {

	public static SpiDelegateHandler get() {
		return Singleton.get(SpiDelegateHandler.class);
	}

	@SecureMethod(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SINGLETON, allowed = {
			SpiBase.class })
	@SecureMethod(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SINGLETON, allowed = {
			SpiLocatorHandler.class })
	Map<SpiType, SpiDelegate<?>> getDelegates();

	
	@SecureMethod(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SAME, allowed = {
			SpiDelegate.class })
	@SecureMethod(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SINGLETON, allowed = {
			SpiBase.class })
	DelegateResorceSet getResources(SpiType type);

	
	@SecureMethod(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SAME, allowed = {
			SpiDelegate.class })
	void releaseResources(SpiType type);
	
	
	@SecureMethod(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SAME, allowed = {
			SpiDelegate.class })
	<T> DelegateInitResult forEach(SpiType type, Function<Class<T>, ResourceStatus> function);

}
