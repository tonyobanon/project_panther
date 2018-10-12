package com.re.paas.api.app_provisioning;

import java.util.Collection;

import javax.json.JsonObject;

import com.re.paas.api.annotations.PlatformInternal;
import com.re.paas.api.classes.KeyValuePair;
import com.re.paas.api.designpatterns.Singleton;

@PlatformInternal
public interface AppProvisioner {
	
	public static AppProvisioner get() {
		return Singleton.get(AppProvisioner.class);
	}
	
	public String defaultAppId();
	
	public void install(String uri);

	public void list();

	public void start();

	public void stop(String app);

	public Collection<String> listApps();

	public AppClassLoader getClassloader(String appId);

	public String getAppId(Class<?> clazz);

	public JsonObject getConfig(String appId);

	public KeyValuePair<String, ClassLoader> getConfiguration(String appId, String key);

}