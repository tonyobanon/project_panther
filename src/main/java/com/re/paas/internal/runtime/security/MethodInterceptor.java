package com.re.paas.internal.runtime.security;

import java.lang.StackWalker.StackFrame;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.ClassLoaderSecurity;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.Platform;
import com.re.paas.internal.jvmtools.annotations.AnnotationUtil;
import com.re.paas.internal.runtime.security.Secure.CustomValidatorContext;
import com.re.paas.internal.runtime.security.Secure.DefaultValidator;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

/**
 * This class intercepts all invocations of methods annotated with
 * {@link Secure}, inorder to validate security constraints
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

		StackWalker sw = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
		ObjectWrapper<StackFrame> frame = new ObjectWrapper<>();

		sw.walk(stackFrames -> {
			frame.set(stackFrames.limit(3).skip(2).findFirst().get());
			return null;
		});

		Class<?> callerClass = frame.get().getDeclaringClass();

		Annotation[] contexts = AnnotationUtil.getAnnotations(method, Secure.class);

		if (debugMode) {
			System.out.println("intercepted call, args=" + Arrays.toString(args) + ", caller-class="
					+ callerClass.getName() + ", caller-method=" + frame.get().getMethodName() + ", target-class="
					+ method.getDeclaringClass() + ", target-method=" + method.getName() + ", annotation="
					+ Arrays.toString(contexts));
		}

		Boolean allow = null;

		for (Annotation annotation : contexts) {
			
			Secure ctx = (Secure) annotation;

			if (!ctx.customValidator().equals(DefaultValidator.class)) {

				DefaultValidator valdator = Singleton.get(ctx.customValidator());

				if (valdator == null) {

					Constructor<? extends DefaultValidator> constructor = null;

					try {
						constructor = ctx.customValidator().getConstructor();
					} catch (Exception e) {
						Exceptions.throwRuntime("A public no-arg constructor is required for " + ctx.customValidator());
					}

					try {
						valdator = constructor.newInstance();
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						Exceptions.throwRuntime(e);
					}

					Singleton.register(ctx.customValidator(), valdator);
				}

				CustomValidatorContext validationCtx = new CustomValidatorContext(frame.get(), (Method) method, args,
						ctx);

				allow = valdator.apply(validationCtx);

			} else {

				switch (ctx.factor()) {

				case CLASSLOADER_SECURITY:
					allow =
							/**
							 * This is done to accommodate our initial file system setup
							 */
							ClassLoaderSecurity.getInstance() == null || ClassLoaderSecurity.hasTrust();
					break;

				case CALLER:

					if (ctx.allowInternalAccess() && callerClass.getName().startsWith(Platform.getPlatformPackage())) {
						allow = true;
					}

					else if (ctx.allowJdkAccess()
							&& Utils.startsWith(callerClass.getName(), Platform.getJvmPackages())) {
						allow = true;
					}

					else if (ctx.allowed().length > 0) {

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

								if (Singleton.get(c).equals(callerClass)) {
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

			if (allow == null || !allow) {

				String source = callerClass.getName() + "#" + frame.get().getMethodName();

				String target = method.getDeclaringClass().getName() + "#" + method.getName() + "("
						+ Arrays.toString(args) + ")";

				throw new SecurityException("Unable to invoke " + target + " from " + source);
			}

		}

		activeContext.set(false);
	}
}
