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
	
	protected abstract Logger setNamespace(String namespace);
	
	public abstract void verboseMode(VerboseLevel verboseLevel);

	public abstract void verboseMode(String verboseLevel);

	public abstract void debug(String msg);

	protected abstract void debug(String namespace, String msg);

	public abstract void error(String msg);

	protected abstract void error(String namespace, String msg);

	public abstract void fatal(String msg);

	protected abstract void fatal(String namespace, String msg);

	public abstract void info(String msg);

	protected abstract void info(String namespace, String msg);

	public abstract void trace(String msg);

	protected abstract void trace(String namespace, String msg);

	public abstract void warn(String msg);

	protected abstract void warn(String namespace, String msg);

	public static enum VerboseLevel {
		OFF, INFO, WARN, ERROR, FATAL, DEBUG, TRACE
	}

}
