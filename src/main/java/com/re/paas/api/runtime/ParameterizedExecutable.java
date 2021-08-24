package com.re.paas.api.runtime;

import java.io.Serializable;
import java.util.function.Function;

import com.re.paas.api.runtime.SecureMethod.Factor;
import com.re.paas.api.runtime.SecureMethod.IdentityStrategy;
import com.re.paas.api.tasks.Affinity;

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
	
	private final Affinity affinity;

	@SecureMethod(factor = Factor.CALLER, allowed = ExecutorFactory.class, identityStrategy = IdentityStrategy.SINGLETON)
	public ParameterizedExecutable(Function<P, R> function, P parameter, Affinity affinity) {
		this.function = function;
		this.parameter = parameter;
		this.affinity = affinity;
	}

	public Function<P, R> getFunction() {
		return function;
	}

	public P getParameter() {
		return parameter;
	}
	
	public Affinity getAffinity() {
		return affinity;
	}
}
