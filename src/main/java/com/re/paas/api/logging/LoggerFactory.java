package com.re.paas.api.logging;

import com.re.paas.api.designpatterns.Singleton;

public interface LoggerFactory {

	public static LoggerFactory get() {
		return Singleton.get(LoggerFactory.class);
	}
	
	public Logger getLog();
	
	public Logger getLog(Class<?> clazz);
	
}
