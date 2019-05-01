package com.re.paas.api.runtime.spi;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.internal.runtime.security.Secure;
import com.re.paas.internal.runtime.spi.AppProvisioner;

public interface SpiBase {
	
	public static SpiBase get() {
		return Singleton.get(SpiBase.class);
	}
	
	public default void start() {
		start(AppProvisioner.DEFAULT_APP_ID);	
	}
	
	@Secure
	public void start(String appId);

	@Secure
	public void stop();

	@Secure
	public Boolean stop(String appId);
	
	@Secure
	public boolean canStop(String appId);

}
