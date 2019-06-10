package com.re.paas.internal.runtime.spi;

import java.nio.file.Path;
import java.util.Set;

import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.runtime.MethodMeta;

public interface AppProvisioner {
	
	public static final String DEFAULT_APP_ID = "default";

	public static AppProvisioner get() {
		return Singleton.get(AppProvisioner.class);
	}
	
	@MethodMeta
	Boolean install(Path archive);

	@MethodMeta
	void list();

	@MethodMeta
	void start();

	@MethodMeta
	void stop(String app);

	@MethodMeta
	Set<String> listApps();

	@MethodMeta
	AppClassLoader getClassloader(String appId);

	@MethodMeta
	JsonObject getConfig(String appId);
	
	@MethodMeta
	String getAppName(String appId);
}