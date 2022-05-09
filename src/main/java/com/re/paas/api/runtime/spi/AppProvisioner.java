package com.re.paas.api.runtime.spi;

import java.nio.file.Path;
import java.util.Set;

import com.re.paas.api.Singleton;
import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.runtime.SecureMethod;

public interface AppProvisioner {
	
	public static final String DEFAULT_APP_ID = "platform";

	public static AppProvisioner get() {
		return Singleton.get(AppProvisioner.class);
	}
	
	@SecureMethod
	void install(Path archive);
	
	@SecureMethod
	void uninstall(String appId);

	@SecureMethod
	void scanApps();

	@SecureMethod
	Set<String> listApps();

	@SecureMethod
	AppClassLoader getClassloader(String appId);

	@SecureMethod
	JsonObject getConfig(String appId);
	
	@SecureMethod
	String getAppName(String appId);
}