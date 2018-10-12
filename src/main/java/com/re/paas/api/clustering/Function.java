package com.re.paas.api.clustering;

import com.re.paas.api.designpatterns.Singleton;

public interface Function {
	
	public static AbstractFunctionDelegate getDelegate() {
		return Singleton.get(AbstractFunctionDelegate.class);
	}
	
	public static short getId(Function function) {
		return getDelegate().getId(function);
	}

	public static Function fromId(Short id) {
		return getDelegate().getFunction(id);
	}
	
	public String namespace();
	
	public short contextId();

	public boolean isAsync();
}
