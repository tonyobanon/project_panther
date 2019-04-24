package com.re.paas.api.runtime.spi;

import com.re.paas.api.annotations.ProtectionContext;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.internal.runtime.spi.AppProvisioner;

public interface SpiBase {
	
	public static SpiBase get() {
		return Singleton.get(SpiBase.class);
	}
	
	public default void start() {
		start(AppProvisioner.DEFAULT_APP_ID);	
	}
	
	@ProtectionContext
	public void start(String appId);

	@ProtectionContext
	public void stop();

	@ProtectionContext
	public Boolean stop(String appId);
	
	@ProtectionContext
	public boolean canStop(String appId);

}
