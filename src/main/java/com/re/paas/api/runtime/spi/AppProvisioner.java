package com.re.paas.api.runtime.spi;

import java.nio.file.Path;
import java.util.Set;

import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.runtime.SecureMethod;

public interface AppProvisioner {
	
	public static final String DEFAULT_APP_ID = "default";

	public static AppProvisioner get() {
		return Singleton.get(AppProvisioner.class);
	}
	
	@SecureMethod
	Boolean install(Path archive);

	@SecureMethod
	void list();

	@SecureMethod
	void start();

	@SecureMethod
	void stop(String app);

	@SecureMethod
	Set<String> listApps();

	@SecureMethod
	AppClassLoader getClassloader(String appId);

	@SecureMethod
	JsonObject getConfig(String appId);
	
	@SecureMethod
	String getAppName(String appId);
}