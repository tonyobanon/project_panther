package com.re.paas.api.runtime;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 
 * This represents a parameterized function that has been packaged for
 * execution.
 * 
 * @author anthonyanyanwu
 *
 * @param <P>
 * @param <R>
 */
public class ParameterizedExecutable<P, R> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Function<P, R> function;
	private final P parameter;

	@SecureMethod
	public ParameterizedExecutable(Function<P, R> function, P parameter) {
		this.function = function;
		this.parameter = parameter;
	}

	public Function<P, R> getFunction() {
		return function;
	}

	public P getParameter() {
		return parameter;
	}
}
