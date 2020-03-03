package com.re.paas.api;

import com.re.paas.api.designpatterns.Singleton;

public interface Activator {

	public static Activator get() {
		return Singleton.get(Activator.class);
	}

	public Boolean isInstalled();
}
