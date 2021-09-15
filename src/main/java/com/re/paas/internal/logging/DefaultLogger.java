package com.re.paas.internal.logging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import com.re.paas.api.Platform;
import com.re.paas.api.annotations.develop.PlatformInternal;
import com.re.paas.api.classes.ThreadContext;
import com.re.paas.api.logging.LogPipeline;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerInterceptor;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.api.utils.Dates;


public class DefaultLogger extends Logger {

	private static final String requestNamespace = "HttpRequest.log";
	private static final String defaultNamespace = "System.log";
	
	private static Boolean isSnapshotEnabled = false;

	private static Long snapshotInitialInterval = 5l;
	private static Long snapshotInterval = 1l;
	private static LoggerInterceptor loggerInterceptor;

	private static LogPipeline logPipeline = null;

	private static Collection<String> logEntries = Collections.synchronizedList(new ArrayList<String>());

	private String namespace;
	
	private boolean isInfoEnabled;
	private boolean isWarnEnabled;
	private boolean isErrorEnabled;
	private boolean isFatalEnabled;
	private boolean isDebugEnabled;
	private boolean isTraceEnabled;

	public DefaultLogger() {
		verboseMode(VerboseLevel.DEBUG.toString());
		this.namespace = defaultNamespace;
	}
	
	private void resetModes() {
		isInfoEnabled = false;
		isWarnEnabled = false;
		isErrorEnabled = false;
		isFatalEnabled = false;
		isDebugEnabled = false;
		isTraceEnabled = false;
	}
	
	@Override
	public boolean enabled() {
		return true;
	}

	@Override
	public Logger verboseMode(VerboseLevel verboseLevel) {
		verboseMode(verboseLevel.toString());
		return this;
	}

	@Override
	public Logger verboseMode(String verboseLevel) {

		VerboseLevel level = null;

		try {
			level = VerboseLevel.valueOf(verboseLevel);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		}

		resetModes();

		switch (level) {
		case OFF:
			break;

		case TRACE:
			isInfoEnabled = true;
			isWarnEnabled = true;
			isErrorEnabled = true;
			isFatalEnabled = true;
			isDebugEnabled = true;
			isTraceEnabled = true;
			break;

		case DEBUG:
			isInfoEnabled = true;
			isWarnEnabled = true;
			isErrorEnabled = true;
			isFatalEnabled = true;
			isDebugEnabled = true;

			break;

		case FATAL:

			isInfoEnabled = true;
			isWarnEnabled = true;
			isErrorEnabled = true;
			isFatalEnabled = true;
			break;

		case ERROR:
			isInfoEnabled = true;
			isWarnEnabled = true;
			isErrorEnabled = true;
			break;

		case WARN:
			isInfoEnabled = true;
			isWarnEnabled = true;
			break;

		case INFO:
			isInfoEnabled = true;
			break;

		default:
			throw new IllegalArgumentException("Unrecognized verbose level");
		}
		
		return this;
	}

	@PlatformInternal
	public static void setPipeline(LogPipeline pipeline) {
		logPipeline = pipeline;
	}
	
	public static LogPipeline getPipeline() {
		return logPipeline;
	}

	public static void setSnapshotEnabled(Boolean isEnabled) {
		isSnapshotEnabled = isEnabled;
	}

	public static void setSnapshotInterval(Long interval) {
		snapshotInterval = interval;
	}

	public static void withLoggerInterceptor(LoggerInterceptor interceptor) {
		loggerInterceptor = interceptor;
	}

	public static Boolean isSnapshotEnabled() {
		return isSnapshotEnabled;
	}

	public static void start() {

		if (isSnapshotEnabled && loggerInterceptor != null) {

			SingleThreadExecutor.scheduleAtFixedRate(() -> {

					// Send Log Entries
					loggerInterceptor.accept(logEntries);

					// Clear Log Entries
					logEntries.clear();

			}, snapshotInitialInterval, snapshotInterval, TimeUnit.SECONDS);

		}
	}
	
	public static void stop() {
		
	}

	private boolean isDebugEnabled() {
		return isDebugEnabled;
	}

	private boolean isErrorEnabled() {
		return isErrorEnabled;
	}

	private boolean isFatalEnabled() {
		return isFatalEnabled;
	}

	private boolean isInfoEnabled() {
		return isInfoEnabled;
	}

	private boolean isTraceEnabled() {
		return isTraceEnabled;
	}

	private boolean isWarnEnabled() {
		return isWarnEnabled;
	}

	private static void logDelegate(VerboseLevel level, String namespace, String message) {
		
		Runnable logToPipeline = () -> {
			String[] lines = format(namespace, message, level);

			if (getPipeline() != null) {
				for (String line : lines) {
					getPipeline().println(level, line);
				}
			}

			if (isSnapshotEnabled) {
				for (String line : lines) {
					logEntries.add(line);
				}
			}
		};
		
		if (Platform.isDevMode()) {
			logToPipeline.run();
		} else {
			SingleThreadExecutor.execute(() -> {
				logToPipeline.run();
			});
		}
	}

	private static final String _getNamespace() {
		return ThreadContext.isWebRequest() ? requestNamespace : defaultNamespace;
	}
	
	@Override
	public String getNamespace() {
		return namespace != null ? namespace : _getNamespace();
	}
	
	@Override
	public Logger setNamespace(String namespace) {
		this.namespace = namespace;
		return this;
	} 
	
	@Override
	public Logger setNamespace(Class<?> clazz, String context) {
		return setNamespace(ClassUtils.asString(clazz) + " (" + context + ")");
	}
	
	@Override
	public void debug(String msg) {
		debug(getNamespace(), msg);
	}

	@Override
	protected void debug(String namespace, String msg) {
		if (isDebugEnabled()) {
			logDelegate(VerboseLevel.DEBUG, namespace, msg);
		}
	}

	@Override
	public void error(String msg) {
		error(getNamespace(), msg);
	}

	@Override
	protected void error(String namespace, String msg) {
		if (isErrorEnabled()) {
			logDelegate(VerboseLevel.ERROR, namespace, msg);
		}
	}

	@Override
	public void fatal(String msg) {
		fatal(getNamespace(), msg);
	}

	@Override
	protected void fatal(String namespace, String msg) {
		if (isFatalEnabled()) {
			logDelegate(VerboseLevel.FATAL, namespace, msg);
		}
	}

	@Override
	public void info(String msg) {
		info(getNamespace(), msg);
	}

	@Override
	protected void info(String namespace, String msg) {
		if (isInfoEnabled()) {
			logDelegate(VerboseLevel.INFO, namespace, msg);
		}
	}

	@Override
	public void trace(String msg) {
		trace(getNamespace(), msg);
	}

	@Override
	protected void trace(String namespace, String msg) {
		if (isTraceEnabled()) {
			logDelegate(VerboseLevel.TRACE, namespace, msg);
		}
	}

	@Override
	public void warn(String msg) {
		warn(getNamespace(), msg);
	}

	@Override
	protected void warn(String namespace, String msg) {
		if (isWarnEnabled()) {
			logDelegate(VerboseLevel.WARN, namespace, msg);
		}
	}

	private static String[] format(String namespace, String msg, VerboseLevel level) {

		String[] lines = msg.split("\\n");

		String[] result = new String[lines.length];
		result[0] = "[" + Dates.now().toString() + "]" + " " + "[" + level.name() + "]" + (namespace != null ? " " + "[" + namespace + "]" : "")
				+ " " + "[" + lines[0] + "]";

		if (lines.length > 1) {
			for (int i = 1; i < lines.length; i++) {
				result[i] = "  " + lines[i];
			}
		}
		return result;
	}
}
