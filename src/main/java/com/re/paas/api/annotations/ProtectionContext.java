package com.re.paas.api.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * 
 * @author anthonyanyanwu
 *
 */
@Retention(RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Repeatable(ProtectionContext.List.class)
/**
 * This annotation should be used on non-final classes
 * 
 * @author anthonyanyanwu
 *
 */
public @interface ProtectionContext {

	/**
	 * The factor determine the code instruction(s) that is automatically inlined
	 * into the function
	 * @return
	 */
	Factor factor() default Factor.THREAD_SECURITY;

	Class<?>[] allowed() default {};

	IdentityStrategy identityStrategy() default IdentityStrategy.ASSIGNABLE;

	/**
	 * If this flag is enabled, and the method is invoked by the class itself, or
	 * any of its ancestors (eg. in the case of default methods in interfaces), we
	 * will automatically allow access
	 * 
	 * @return
	 */
	boolean allowInternal() default true;
	
	boolean allowJdkAccess() default false;

	Class<? extends Function<CustomValidatorContext, Boolean>> customValidator() default ProtectionContext.DefaultValidator.class;
	
	public static enum Factor {
		THREAD_SECURITY, CALLER
	}

	public static enum IdentityStrategy {
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
		ProtectionContext[] value();
	}
	
	public static class CustomValidatorContext {
		
		private final Class<?> caller;
		private final Method method;
		private final Object[] parameters;
		
		public CustomValidatorContext(Class<?> caller, Method method, Object[] parameters) {
			super();
			this.caller = caller;
			this.method = method;
			this.parameters = parameters;
		}

		public Class<?> getCaller() {
			return caller;
		}

		public Method getMethod() {
			return method;
		}

		public Object[] getParameters() {
			return parameters;
		}
	}

	public static class DefaultValidator implements Function<CustomValidatorContext, Boolean> {
		@Override
		public Boolean apply(CustomValidatorContext t) {
			return true;
		}
	}
	
}
