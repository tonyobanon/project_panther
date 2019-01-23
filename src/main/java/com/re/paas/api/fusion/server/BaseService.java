package com.re.paas.api.fusion.server;

import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.fusion.services.AbstractServiceDelegate;
import com.re.paas.api.models.BaseModel;

@BlockerTodo("Add helper method here to check param instead of always checking for undefined")
public abstract class BaseService {

	public abstract String uri();

	@SuppressWarnings("unchecked")
	public Class<? extends BaseModel>[] externalModels() {
		return new Class[] {};
	}

	protected final String getLocationHeader() {
		return "X-Location";
	}

	public static AbstractServiceDelegate getDelegate() {
		return Singleton.get(AbstractServiceDelegate.class);
	}

}
