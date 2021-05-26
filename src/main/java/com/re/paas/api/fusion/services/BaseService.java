package com.re.paas.api.fusion.services;

import com.re.paas.api.Singleton;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.SpiType;

public abstract class BaseService extends AbstractResource {

	public BaseService() {
		super(SpiType.SERVICE);
	}
	
	public String uri() {
		return "";
	}

	public static AbstractServiceDelegate getDelegate() {
		return Singleton.get(AbstractServiceDelegate.class);
	}

}
