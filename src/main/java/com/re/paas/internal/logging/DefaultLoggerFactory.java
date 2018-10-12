package com.re.paas.internal.logging;

import com.re.paas.api.logging.LogPipeline;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;

public class DefaultLoggerFactory implements LoggerFactory {

	@Override
	public Logger getLog(Class<?> clazz) {
		return new DefaultLogger().setNamespace(clazz.getSimpleName());
	}
	
	@Override
	public Logger getLog() {
		return new DefaultLogger();
	}
	
	static {
		DefaultLogger.setPipeline(LogPipeline.from(System.out, System.err));
	}

}
