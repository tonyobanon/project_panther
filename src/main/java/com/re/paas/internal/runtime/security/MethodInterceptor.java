package com.re.paas.internal.runtime.security;

import java.lang.StackWalker.StackFrame;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.ClassLoaderSecurity;
import com.re.paas.api.runtime.MethodMeta;
import com.re.paas.api.runtime.MethodMeta.CustomValidatorContext;
import com.re.paas.api.runtime.MethodMeta.Validator;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.Platform;
import com.re.paas.internal.jvmtools.annotations.ClassTools;
import com.re.paas.internal.jvmtools.repository.JvmConstants;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

/**
 * This class intercepts all invocations of methods annotated with
 * {@link MethodMeta}, inorder to validate security constraints
 * 
 * @author anthonyanyanwu
 *
 */
public class MethodInterceptor {

	private static final Boolean debugMode = false;
	private static ThreadLocal<Boolean> activeContext = ThreadLocal.withInitial(() -> false);

	/**
	 * When walking the stack frames , we would normally limit to 3 items and skip 2
	 * entries.. However, we need to accommodate scenarios where synthetic methods
	 * would be contained in the stack frames stream (which needs to be skipped)
	 */
	private static final int SYNTHETIC_METHOD_FRAME_OFFSET = 5;

	@RuntimeType
	public static void intercept(@Origin Executable method, @AllArguments Object[] args) {

		if (activeContext.get()) {
			return;
		}

		activeContext.set(true);
		SecurityManagerImpl.activeContext.set(true);

		StackWalker sw = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
		ObjectWrapper<StackFrame> frame = new ObjectWrapper<>();

		sw.walk(stackFrames -> {

			Iterator<StackFrame> it = stackFrames.limit(3 + SYNTHETIC_METHOD_FRAME_OFFSET).skip(2).iterator();

			while (it.hasNext()) {
				StackFrame f = it.next();

				if (!f.getMethodName().startsWith(JvmConstants.LAMBDA_SYNTHETIC_METHOD_PREFIX)) {
					frame.set(f);
					return null;
				}
			}
			return null;
		});

		if (frame.get() == null) {
			Exceptions.throwRuntime("The stack frames represent more than the allowed maximum of "
					+ SYNTHETIC_METHOD_FRAME_OFFSET + " synthetic methods");
		}

		
		Class<?> callerClass = frame.get().getDeclaringClass();
		
		/**
		 * If caller originates from the same class, continue
		 */
		if(callerClass.equals(method.getDeclaringClass())) {
			
			activeContext.set(false);
			SecurityManagerImpl.activeContext.set(false);
			
			return;
		}

		/**
		 * Get annotations
		 */
		Annotation[] contexts = ClassTools.getAnnotations(method, MethodMeta.class);

		if (debugMode) {
			System.out.println("intercepted call, args=" + Arrays.toString(args) + ", caller-class="
					+ callerClass.getName() + ", caller-method=" + frame.get().getMethodName() + ", target-class="
					+ method.getDeclaringClass() + ", target-method=" + method.getName() + ", annotation="
					+ Arrays.toString(contexts));
		}

		Boolean allow = null;

		for (Annotation annotation : contexts) {

			MethodMeta ctx = (MethodMeta) annotation;
			
			if (ctx.allowInternalAccess() && callerClass.getName().startsWith(Platform.getPlatformPackage())) {
				allow = true;
			}

			else if (ctx.allowJdkAccess()
					&& Utils.startsWith(callerClass.getName(), Platform.getJvmPackages())) {
				allow = true;
			} 
			else if (!ctx.validator().equals(Validator.class)) {

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

				CustomValidatorContext validationCtx = new CustomValidatorContext(frame.get(), (Method) method, args,
						ctx);

				allow = validator.apply(validationCtx);

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

								if (Singleton.get(c).getClass().equals(callerClass)) {
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
		SecurityManagerImpl.activeContext.set(false);
	}
}
