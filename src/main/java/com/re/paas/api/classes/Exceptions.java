package com.re.paas.api.classes;

import com.re.paas.api.Platform;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.errors.Error;
import com.re.paas.api.logging.Logger;
import com.re.paas.internal.logging.DefaultLogger;

@BlockerTodo("Add metrics to indicate application error counts")
public class Exceptions {
	
	private static final Logger LOG = new DefaultLogger();

	private static void output(String header, String body, boolean isFatal) {

		String o = "\n" + (header != null ? header : "") + body != null ? "\n" + body : "";
		if (isFatal) {
			LOG.fatal(o);
		} else {
			LOG.error(o);
		}
	}

	public static Object throwRuntime(String msg) {
		return throwRuntime(new RuntimeException(msg));
	}
	
	public static Object throwRuntime(String message, Throwable t) {

		String header = message != null ? message : t.getMessage();
		String body = getStackTrace(recurseCause(t));

		if(!Platform.isDevMode()) {
			output(header, body, true);
		}
		throw new RuntimeException(t);
	}
	
	public static Object throwRuntime(Throwable t) {
		return throwRuntime(null, t);
	}

	public static Object throwRuntime(PlatformException err) {
		Error error = err.getError();

		Boolean isRequestCtx = ThreadContext.isWebRequest();
		
		String header = err.getMessage();
		String body = error.isFatal() ? getStackTrace(err) : null;
		boolean isFatal = error.isFatal() && !isRequestCtx;

		output(header, body, isFatal);
		
		throw err;
	}

	private static String getStackTrace(Throwable t) {
		StringBuilder stackTrace = new StringBuilder();
		stackTrace.append(t.getMessage()).append("\n");
		for (StackTraceElement o : t.getStackTrace()) {
			stackTrace.append("\t").append(o.toString()).append("\n");
		}
		stackTrace.append("\n");
		return stackTrace.toString();
	}

	public static Throwable recurseCause(Throwable t) {

		if (t.getCause() != null) {
			return recurseCause(t.getCause());
		} else {
			return t;
		}
	}

}
