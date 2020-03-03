package com.re.paas.api.runtime;

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
 * @author anthony.anyanwu
 *
 */
@Retention(RUNTIME)
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
@Repeatable(SecureMethod.List.class)

public @interface SecureMethod {

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
	 * any of its descendants (eg. in the case of default methods in interfaces), we
	 * will automatically allow access
	 * 
	 * @return
	 */
	boolean restrictHierarchyAccess() default false;
	
	/**
	 * This indicates whether the only the jvm should be allowed access to this.
	 * This is an all or none setting
	 * @return
	 */
	boolean jvmOnly() default false;

	Class<? extends Validator> validator() default Validator.class;

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
	@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
	public @interface List {
		SecureMethod[] value();
	}

	public static class CustomValidatorContext {

		private final StackFrame source;
		private final Method target;
		private final Object[] arguments;
		private final SecureMethod ctx;

		public CustomValidatorContext(StackFrame source, Method target, Object[] arguments, SecureMethod ctx) {
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

		public SecureMethod getCtx() {
			return ctx;
		}
	}

	public static class Validator implements Function<CustomValidatorContext, Boolean> {
		@Override
		public Boolean apply(CustomValidatorContext t) {
			return true;
		}
	}
}
