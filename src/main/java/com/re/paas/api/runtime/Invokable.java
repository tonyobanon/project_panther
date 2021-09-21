package com.re.paas.api.runtime;

import java.io.Serializable;

import com.re.paas.api.annotations.AppClassLoaderInstrinsic;

@FunctionalInterface
@AppClassLoaderInstrinsic
public interface Invokable extends Serializable, Runnable {
	
	public abstract void run();
	
}
