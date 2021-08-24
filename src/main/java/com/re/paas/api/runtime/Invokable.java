package com.re.paas.api.runtime;

import com.re.paas.api.annotations.AppClassLoaderInstrinsic;

@FunctionalInterface
@AppClassLoaderInstrinsic
public interface Invokable {
	
	public abstract void run();
	
}
