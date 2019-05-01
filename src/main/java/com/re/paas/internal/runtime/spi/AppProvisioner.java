package com.re.paas.internal.runtime.spi;

import java.nio.file.Path;
import java.util.Set;

import com.google.gson.JsonObject;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.internal.runtime.security.Secure;

public interface AppProvisioner {
	
	public static final String DEFAULT_APP_ID = "default";

	public static AppProvisioner get() {
		return Singleton.get(AppProvisioner.class);
	}
	
	@Secure
	public boolean install(Path archive);

	@Secure
	public void list();

	@Secure
	public void start();

	@Secure
	public void stop(String app);

	@Secure
	public Set<String> listApps();

	@Secure
	public AppClassLoader getClassloader(String appId);

	@Secure
	public JsonObject getConfig(String appId);
}