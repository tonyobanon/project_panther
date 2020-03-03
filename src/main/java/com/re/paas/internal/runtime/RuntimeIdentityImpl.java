package com.re.paas.internal.runtime;

import java.lang.StackWalker.StackFrame;

import com.re.paas.api.annotations.AppClassLoaderInstrinsic;
import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.runtime.ClassLoaders;
import com.re.paas.api.runtime.RuntimeIdentity;
import com.re.paas.api.runtime.spi.AppProvisioner;
import com.re.paas.api.runtime.spi.SpiBase;

@AppClassLoaderInstrinsic
public class RuntimeIdentityImpl extends RuntimeIdentity {

	private ClassLoader mainClassloader;

	public RuntimeIdentityImpl() {
	}

	public RuntimeIdentityImpl(ClassLoader cl) {
		mainClassloader = cl;
	}

	@Override
	public Boolean isPlatformExternal() {
		return mainClassloader != null;
	}

	@Override
	public Boolean isTrusted(Integer skipsOffset) {

		if (!isPlatformExternal()) {

			// To improve performance, we can first check if this is invoked
			// in a non-external context, and immediately return

			return true;
		}

		return isTrusted(StackFrameUtilImpl.getCaller(skipsOffset + 1, false, true));
	}

	@Override
	public Boolean isTrusted(StackFrame frame) {

		Class<?> callerClass = frame.getDeclaringClass();

		String appId = ClassLoaders.getId(callerClass);

		return

		// This is either a Jvm class, or a platform class
		appId.equals(AppProvisioner.DEFAULT_APP_ID) ||

		// This is a third party class that is trusted
				SpiBase.get().hasTrust(appId);
	}

	@Override
	protected String getApplicationId() {

		ClassLoader cl = RuntimeIdentityImpl.class.getClassLoader();
		String appId = AppProvisioner.DEFAULT_APP_ID;

		if (cl instanceof AppClassLoader) {
			appId = ((AppClassLoader) cl).getAppId();
		}

		return appId;
	}
}
