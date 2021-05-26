package com.re.paas.api.runtime;

import java.lang.StackWalker.StackFrame;

import com.re.paas.api.annotations.AppClassLoaderInstrinsic;
import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.internal.runtime.RuntimeIdentityImpl;

@AppClassLoaderInstrinsic
public abstract class RuntimeIdentity {

	private static RuntimeIdentity instance;

	public static RuntimeIdentity getInstance() {
		return instance;
	}

	public static void setInstance(RuntimeIdentity instance) {
		if (RuntimeIdentity.instance == null) {
			RuntimeIdentity.instance = instance;
		}
	}

	public static String getAppId() {
		return getInstance().getApplicationId();
	}
	
	public static Boolean isExternalContext() {
		return getInstance() != null ? getInstance().isPlatformExternal() : false;
	}

	protected abstract String getApplicationId();

	public abstract Boolean isPlatformExternal();

	public abstract Boolean isTrusted(Integer skipsOffset);
	
	public abstract Boolean isTrusted(StackFrame frame);
	
	static {
		ClassLoader cl = RuntimeIdentity.class.getClassLoader();
		instance = new RuntimeIdentityImpl(cl instanceof AppClassLoader ? cl : null);
	}
	
}