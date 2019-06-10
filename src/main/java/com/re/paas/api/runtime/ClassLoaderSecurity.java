package com.re.paas.api.runtime;

import java.lang.reflect.InvocationTargetException;

import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.internal.runtime.security.ClassLoaderSecurityImpl;
import com.re.paas.internal.runtime.spi.AppProvisionerImpl;


public abstract class ClassLoaderSecurity {

	private static ClassLoaderSecurity instance;

	public static ClassLoaderSecurity getInstance() {
		return instance;
	}

	public static void setInstance(ClassLoaderSecurity instance) {
		if (ClassLoaderSecurity.instance == null) {
			ClassLoaderSecurity.instance = instance;
		}
	}

	public static String getAppId() {

		ClassLoader cl = ClassLoaderSecurity.class.getClassLoader();
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
	 * This loads the {@link ClassLoaderSecurity} class into the specified 
	 * {@link AppClassLoader}
	 */
	public static void load(AppClassLoader cl) {

		try {

			@SuppressWarnings("unchecked")
			Class<ClassLoaderSecurityImpl> tscImpl = (Class<ClassLoaderSecurityImpl>) cl
					.loadClass(ClassLoaderSecurityImpl.class.getName());

			@SuppressWarnings("unchecked")
			Class<ClassLoaderSecurity> tsc = (Class<ClassLoaderSecurity>) cl.loadClass(ClassLoaderSecurity.class.getName());

			tsc.getDeclaredMethod("setInstance", tsc).invoke(null,
					/**
					 * Instantiate a custom instance
					 */
					tscImpl.getDeclaredConstructor(ClassLoader.class)
							.newInstance(ClassLoader.getSystemClassLoader()));

		} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | InstantiationException e) {
			Exceptions.throwRuntime(e);
		}
	}

	public abstract Boolean isTrusted();
	
	public abstract <R> Thread newThread(Invokable<R> i);
	
}