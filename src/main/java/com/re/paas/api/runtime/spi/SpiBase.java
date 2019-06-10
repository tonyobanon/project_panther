package com.re.paas.api.runtime.spi;

import java.util.Collection;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.MethodMeta;

public interface SpiBase {
	
	public static SpiBase get() {
		return Singleton.get(SpiBase.class);
	}
	
	@MethodMeta
	public void start(Collection<String> apps);

	@MethodMeta
	public void stop();

	@MethodMeta
	public Boolean stop(String appId);

}
