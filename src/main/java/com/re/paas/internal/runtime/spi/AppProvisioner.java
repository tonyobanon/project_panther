package com.re.paas.internal.runtime.spi;

import java.nio.file.Path;
import java.util.Set;

import com.google.gson.JsonObject;
import com.re.paas.api.annotations.ProtectionContext;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.designpatterns.Singleton;

public interface AppProvisioner {
	
	public static final String DEFAULT_APP_ID = "default";

	public static AppProvisioner get() {
		return Singleton.get(AppProvisioner.class);
	}
	
	@ProtectionContext
	public boolean install(Path archive);

	@ProtectionContext
	public void list();

	@ProtectionContext
	public void start();

	@ProtectionContext
	public void stop(String app);

	@ProtectionContext
	public Set<String> listApps();

	@ProtectionContext
	public AppClassLoader getClassloader(String appId);

	@ProtectionContext
	public JsonObject getConfig(String appId);
}