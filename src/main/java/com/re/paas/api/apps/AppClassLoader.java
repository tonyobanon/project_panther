package com.re.paas.api.apps;

import java.net.URLClassLoader;
import java.nio.file.Path;

import com.re.paas.api.runtime.SecureMethod;

public abstract class AppClassLoader extends ClassLoader {

	public AppClassLoader(ClassLoader parent) {
		super(parent);
	}

	@SecureMethod
	public abstract Path getPath();

	public abstract String getAppId();

	public abstract boolean isStopping();
	
	protected abstract Class<?> findClass(String name) throws ClassNotFoundException;

	@SecureMethod
	public abstract void setStopping(boolean isStopping);
	
	@SecureMethod
	public abstract URLClassLoader getLibraryClassLoader();

}
