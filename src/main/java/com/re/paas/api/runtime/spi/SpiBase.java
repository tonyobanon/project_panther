package com.re.paas.api.runtime.spi;

import java.util.Collection;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.SecureMethod;

public interface SpiBase {
	
	public static SpiBase get() {
		return Singleton.get(SpiBase.class);
	}
	
	@SecureMethod
	public void start(Collection<String> apps);

	@SecureMethod
	public void stop();

	@SecureMethod
	public Boolean stop(String appId);
	
	@SecureMethod
	public Boolean hasTrust(String appId);

}
