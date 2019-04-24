package com.re.paas.api.runtime;

import java.lang.reflect.InvocationTargetException;

import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.internal.runtime.ThreadSecurityImpl;
import com.re.paas.internal.runtime.spi.AppProvisionerImpl;


public abstract class ThreadSecurity {

	private static ThreadSecurity instance;

	public static ThreadSecurity getInstance() {
		return instance;
	}

	public static void setInstance(ThreadSecurity instance) {
		if (ThreadSecurity.instance == null) {
			ThreadSecurity.instance = instance;
		}
	}

	public static String getAppId() {

		ClassLoader cl = ThreadSecurity.class.getClassLoader();
		String appId = AppProvisionerImpl.DEFAULT_APP_ID;

		if (cl instanceof AppClassLoader) {
			appId = ((AppClassLoader) cl).getAppId();
		}

		return appId;
	}

	public static boolean hasTrust() {
		return getInstance().isTrusted();
	}

	/**
	 * This loads the {@link ThreadSecurity} class into the specified 
	 * {@link AppClassLoader}
	 */
	public static void load(AppClassLoader cl) {

		try {

			@SuppressWarnings("unchecked")
			Class<ThreadSecurityImpl> tscImpl = (Class<ThreadSecurityImpl>) cl
					.loadClass(ThreadSecurityImpl.class.getName());

			@SuppressWarnings("unchecked")
			Class<ThreadSecurity> tsc = (Class<ThreadSecurity>) cl.loadClass(ThreadSecurity.class.getName());

			tsc.getDeclaredMethod("setInstance", tsc).invoke(null,
					/**
					 * Instantiate a custom instance
					 */
					tscImpl.getDeclaredConstructor(ClassLoader.class, Boolean.class)
							.newInstance(ClassLoader.getSystemClassLoader(), false));

		} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | InstantiationException e) {
			Exceptions.throwRuntime(e);
		}
	}

	public abstract Boolean isTrusted();
	
	public abstract <R> Thread newThread(Invokable<R> i);
	
}