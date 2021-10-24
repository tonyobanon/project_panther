package com.re.paas.api.runtime.spi;


import com.re.paas.api.Singleton;
import com.re.paas.api.runtime.SecureMethod;

public interface SpiBase {
	
	public static SpiBase get() {
		return Singleton.get(SpiBase.class);
	}
	
	@SecureMethod
	public Boolean hasTrust(String appId);
}
