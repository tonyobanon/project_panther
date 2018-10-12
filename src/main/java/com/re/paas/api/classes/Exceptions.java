package com.re.paas.api.classes;

import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.errors.Error;
import com.re.paas.api.logging.Logger;

@BlockerTodo("Add metrics to indicate application error counts")
public class Exceptions {

	private static void output(String header, String body, boolean isFatal) {

		String o = "\n" + (header != null ? header : "") + body != null ? "\n" + body : "";
		if (isFatal) {
			Logger.get().fatal(o);
		} else {
			Logger.get().error(o);
		}
	}

	public static Object throwRuntime(String msg) {
		return throwRuntime(new RuntimeException(msg));
	}
	
	public static Object throwRuntime(String message, Throwable t) {

		String header = message != null ? message : t.getMessage();
		String body = getStackTrace(recurseCause(t));

		output(header, body, true);
		throw new RuntimeException(t);
	}
	
	public static Object throwRuntime(Throwable t) {
		return throwRuntime(null, t);
	}

	public static Object throwRuntime(PlatformException err) {
		Error error = err.getError();

		Boolean isRequestCtx = ThreadContext.isRequestContext();
		
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
