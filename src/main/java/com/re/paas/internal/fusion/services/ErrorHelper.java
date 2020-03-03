package com.re.paas.internal.fusion.services;

import com.re.paas.api.classes.Exceptions;

public class ErrorHelper {

	/**
	 * In our models, we don't explicitly catch exceptions for entities, therefore
	 * the throwable bubbles up to BaseApiServlet, and GAE wraps the throwable as an
	 * InvocationTargetException. This function then finds the real exception that
	 * occurred, before it was wrapped, and displays a returns a suitable error
	 * message
	 */
	static Throwable getError(Exception e) {

		Throwable t = Exceptions.recurseCause(e);
		String className = t.getClass().getName();

		switch (className) {

//		case "com.googlecode.objectify.NotFoundException":
//			return new RuntimeException("No entity was found with the specified key.");

		default:
			return t;
		}
	}

}
