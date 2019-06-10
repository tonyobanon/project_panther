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

import com.re.paas.internal.runtime.security.MetaFactory;

/**
 * 
 * This annotation provides access to method configuration parameters as defined
 * by the {@link MetaFactory} <br>
 * <br>
 * There are currently two definition models: <br>
 * <br>
 * 
 * <b>Transitive </b><br>
 * This means that the annotation will not directly be applied to the annotated method,
 * but instead to methods in its subclasses (post meta factory scanning)
 * 
 * <br><br>
 * 
 * <b>Direct </b><br>
 * Here, the the annotation will be directly applied to the annotated method
 * 
 * @author anthonyanyanwu
 *
 */
@Retention(RUNTIME)
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
@Repeatable(MethodMeta.List.class)

public @interface MethodMeta {

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
	boolean allowInternalAccess() default false;

	boolean allowJdkAccess() default false;

	Class<? extends Validator> validator() default Validator.class;

	public static enum Factor {
		CLASSLOADER_SECURITY, CALLER
	}

	/**
	 * If true, the annotated non-static non-final method is moved to concrete
	 * subclasses of the declaring class. Note that since {@link MethodMeta} is
	 * repeatable, this property will be read from the first occurrences if there
	 * are multiple occurrences of {@link MethodMeta} on a single method
	 * 
	 * @return
	 */
	boolean onImplementation() default false;

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
		MethodMeta[] value();
	}

	public static class CustomValidatorContext {

		private final StackFrame source;
		private final Method target;
		private final Object[] arguments;
		private final MethodMeta ctx;

		public CustomValidatorContext(StackFrame source, Method target, Object[] arguments, MethodMeta ctx) {
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

		public MethodMeta getCtx() {
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
