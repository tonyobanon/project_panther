package com.re.paas.api.logging;

public abstract class Logger {
	
	public static Logger get(Class<?> clazz) {
		return LoggerFactory.get().getLog(clazz);
	}

	public static Logger get() {
		return LoggerFactory.get().getLog();
	}

	public abstract boolean enabled();

	public abstract String getNamespace();
	
	public abstract Logger setNamespace(String namespace);
	
	public abstract Logger setNamespace(Class<?> clazz, String context);
	
	public abstract Logger verboseMode(VerboseLevel verboseLevel);

	public abstract void debug(String msg, Object...args);

	public abstract void error(String msg, Object...args);

	public abstract void fatal(String msg, Object...args);

	public abstract void info(String msg, Object...args);

	public abstract void trace(String msg, Object...args);

	public abstract void warn(String msg, Object...args);

	public static enum VerboseLevel {
		OFF, INFO, WARN, ERROR, FATAL, DEBUG, TRACE
	}

}
