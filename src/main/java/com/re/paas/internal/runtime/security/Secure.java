package com.re.paas.internal.runtime.security;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.StackWalker.StackFrame;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * 
 * This annotation should be used on methods to enable access control <br>
 * <br>
 * 
 * ImplNotes:<br>
 * This annotation should be used on non-final elements <br>
 * 
 * @author anthonyanyanwu
 *
 */
@Retention(RUNTIME)
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
@Repeatable(Secure.List.class)

public @interface Secure {

	/**
	 * The factor determine the criteria to use to allow invocation
	 * 
	 * @return
	 */
	Factor factor() default Factor.CLASSLOADER_SECURITY;

	Class<?>[] allowed() default {};

	IdentityStrategy identityStrategy() default IdentityStrategy.ASSIGNABLE;

	/**
	 * If this flag is enabled, and the method is invoked by the class itself, or
	 * any of its ancestors (eg. in the case of default methods in interfaces), we
	 * will automatically allow access
	 * 
	 * @return
	 */
	boolean allowInternalAccess() default true;

	boolean allowJdkAccess() default false;

	Class<DefaultValidator> customValidator() default DefaultValidator.class;

	public static enum Factor {
		CLASSLOADER_SECURITY, CALLER
	}

	public static enum IdentityStrategy {
		/**
		 * If the allowed class is same as the caller class
		 */
		SAME,
		
		/**
		 * If the allowed class is assignable from caller class is a subtype
		 */
		ASSIGNABLE,

		/**
		 * If the caller class is the singleton class of the allowed class
		 */
		SINGLETON
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface List {
		Secure[] value();
	}

	public static class CustomValidatorContext {

		private final StackFrame source;
		private final Method target;
		private final Object[] arguments;
		private final Secure ctx;

		public CustomValidatorContext(StackFrame source, Method target, Object[] arguments, Secure ctx) {
			super();
			this.source = source;
			this.target = target;
			this.arguments = arguments;
			this.ctx = ctx;
		}

		public StackFrame getSource() {
			return source;
		}

		public Method getTarget() {
			return target;
		}

		public Object[] getArguments() {
			return arguments;
		}

		public Secure getCtx() {
			return ctx;
		}
	}

	public static class DefaultValidator implements Function<CustomValidatorContext, Boolean> {
		@Override
		public Boolean apply(CustomValidatorContext t) {
			return true;
		}
	}

}
