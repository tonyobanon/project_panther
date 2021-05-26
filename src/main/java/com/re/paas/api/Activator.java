package com.re.paas.api;

public interface Activator {

	public static Activator get() {
		return Singleton.get(Activator.class);
	}

	public Boolean isInstalled();
	
	public void restartNeeded();
	
}
