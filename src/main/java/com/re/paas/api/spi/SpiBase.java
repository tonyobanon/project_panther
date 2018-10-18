package com.re.paas.api.spi;

import com.re.paas.api.annotations.PlatformInternal;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.internal.app_provisioning.AppProvisioner;

@PlatformInternal
public interface SpiBase {
	
	public static SpiBase get() {
		return Singleton.get(SpiBase.class);
	}
	
	public default void start() {
		start(AppProvisioner.get().defaultAppId());	
	}
	
	public void start(String appId);

	public void stop();

	public void stop(String appId);

	public String getLocatorConfigKey(SpiTypes type);

	public String getDelegateConfigKey(SpiTypes type);
	
	public boolean canStop(String appId);

}
