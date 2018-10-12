package com.re.paas.api.app_provisioning;

import java.nio.file.Path;

import com.re.paas.api.annotations.PlatformInternal;
import com.re.paas.api.designpatterns.Factory;

@PlatformInternal
public abstract class AppClassLoader extends ClassLoader {

	private static DelegationType delegateType;
	
	public AppClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	public static void setDelegationType(DelegationType type) {
		if(delegateType == null) {
			delegateType = type;
		}
	}
	
	public static DelegationType getDelegationType() {
		return delegateType;
	}
	
	public static AppClassLoader get(Path path, String appId, String[] dependencies) {
		return Factory.get(AppClassLoader.class, new Object[] {path, appId, dependencies});
	}
	
	public abstract Path getPath();

	public abstract String getAppId();

	public abstract boolean isStopping();

	public abstract void setStopping(boolean isStopping);
	
	static {
		setDelegationType(DelegationType.DELEGATE_FIRST);
	}
	
	public static enum DelegationType {
		 DELEGATE_FIRST, DELEGATE_LATER
	}
}
