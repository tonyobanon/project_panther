package com.re.paas.internal.logging;

import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.logging.Logger.VerboseLevel;

public class DefaultLoggerFactory implements LoggerFactory {

	@Override
	public Logger getLog(Class<?> clazz) {
		return new DefaultLogger().setNamespace(clazz.getSimpleName()).verboseMode(VerboseLevel.DEBUG);
	}

	@Override
	public Logger getLog() {
		return new DefaultLogger().verboseMode(VerboseLevel.TRACE);
	}
}
