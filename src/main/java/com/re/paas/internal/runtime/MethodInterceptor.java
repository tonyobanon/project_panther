package com.re.paas.internal.runtime;

import java.lang.StackWalker.StackFrame;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.re.paas.api.Platform;
import com.re.paas.api.Singleton;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.api.runtime.SecureMethod.CustomValidatorContext;
import com.re.paas.api.runtime.SecureMethod.Validator;
import com.re.paas.api.runtime.RuntimeIdentity;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.instrumentation.BytecodeTools;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

/**
 * This class intercepts all invocations of methods annotated with
 * {@link SecureMethod}, inorder to validate security constraints
 * 
 * @author anthonyanyanwu
 *
 */
public class MethodInterceptor {

	private static final Boolean debugMode = false;
	private static ThreadLocal<Boolean> activeContext = ThreadLocal.withInitial(() -> false);

	@RuntimeType
	public static void intercept(@Origin Executable method, @AllArguments Object[] args) {

		if (activeContext.get()) {
			return;
		}

		activeContext.set(true);
		SecurityManagerImpl.activeContext.set(true);

		/**
		 * Get annotations
		 */
		Annotation[] contexts = BytecodeTools.getAnnotations(method.getAnnotations(), SecureMethod.class);

		if (debugMode) {
			System.out.println(
					"intercepted call, args=" + Arrays.toString(args) + ", target-class=" + method.getDeclaringClass()
							+ ", target-method=" + method.getName() + ", annotation=" + Arrays.toString(contexts));
		}

		Boolean allow = null;

		for (Annotation annotation : contexts) {

			SecureMethod ctx = (SecureMethod) annotation;

			StackFrame frame = StackFrameUtilImpl.getCaller(3, ctx.jvmOnly(), true);

			Class<?> callerClass = frame.getDeclaringClass();

			if (debugMode) {
				System.out.println("-- context: " + "caller-class=" + callerClass.getName() + ", caller-method="
						+ frame.getMethodName());
			}

			if (
			// If caller originates exactly from the same class
			callerClass.equals(method.getDeclaringClass()) ||

			// or if hierarchy access is allowed and a subclass tries to access a method
			// from the super class
					((!ctx.restrictHierarchyAccess()) && method.getDeclaringClass().isAssignableFrom(callerClass))

			) {
				allow = true;
			}

			// If a jvmOnly restriction exists, enforce that here
			if (ctx.jvmOnly()) {

				allow = Utils.startsWith(callerClass.getName(), Platform.getJvmPackages());
			}

			if (allow == null && !ctx.validator().equals(Validator.class)) {

				Validator validator = Singleton.get(ctx.validator());

				if (validator == null) {

					Constructor<? extends Validator> constructor = null;

					try {
						constructor = ctx.validator().getConstructor();
					} catch (Exception e) {
						Exceptions.throwRuntime("A public no-arg constructor is required for " + ctx.validator());
					}

					try {
						validator = constructor.newInstance();
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						Exceptions.throwRuntime(e);
					}

					Singleton.register(ctx.validator(), validator);
				}

				CustomValidatorContext validationCtx = new CustomValidatorContext(frame, (Method) method, args, ctx);

				allow = validator.apply(validationCtx);

			}

			if (allow == null) {

				switch (ctx.factor()) {

				case CLASSLOADER_SECURITY:
					allow =
							/**
							 * This is done to accommodate our initial file system setup
							 */
							RuntimeIdentity.getInstance() == null || RuntimeIdentity.getInstance().isTrusted(frame);
					break;

				case CALLER:

					if (ctx.allowed().length > 0) {

						for (Class<?> c : ctx.allowed()) {

							switch (ctx.identityStrategy()) {

							case SAME:

								if (c.equals(callerClass)) {
									allow = true;
									break;
								}

								break;

							case ASSIGNABLE:

								if (c.isAssignableFrom(callerClass)) {
									allow = true;
									break;
								}

								break;

							case SINGLETON:

								if (Singleton.get(c) != null && Singleton.get(c).getClass().equals(callerClass)) {
									allow = true;
									break;
								}

								break;
							}
						}
					}

					break;
				}
			}
			
			if (allow == null) {
				allow = false;
			}

			if (allow) {
				break;
				
			} else {

				String source = callerClass.getName() + "#" + frame.getMethodName();

				String target = method.getDeclaringClass().getName() + "#" + method.getName() + "("
						+ Arrays.toString(args) + ")";

				throw new SecurityException("Unable to invoke " + target + " from " + source);
			}

		}

		activeContext.set(false);
		SecurityManagerImpl.activeContext.set(false);
	}
}
