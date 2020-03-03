package com.re.paas.api.apps;

import java.nio.file.Path;

import com.re.paas.api.designpatterns.Factory;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.runtime.SecureMethod;

public abstract class AppClassLoader extends ClassLoader {

	private static Logger LOG = LoggerFactory.get().getLog(AppClassLoader.class);

	public AppClassLoader(ClassLoader parent) {
		super(parent);
	}

	public static AppClassLoader get(String appId) {
		return Factory.get(AppClassLoader.class, new Object[] { appId });
	}

	@SecureMethod
	public abstract Path getPath();

	public abstract String getAppId();

	public abstract boolean isStopping();
	
	protected abstract Class<?> findClass(String name) throws ClassNotFoundException;

	@SecureMethod
	public abstract void setStopping(boolean isStopping);

}
