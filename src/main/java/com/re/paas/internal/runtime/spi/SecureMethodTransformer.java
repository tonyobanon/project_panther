package com.re.paas.internal.runtime.spi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.internal.instrumentation.BytecodeTools;
import com.re.paas.internal.runtime.MethodInterceptor;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.attribute.AnnotationRetention;
import net.bytebuddy.matcher.ElementMatcher;

import static com.re.paas.api.utils.ClassUtils.getName;

public class SecureMethodTransformer extends ClassTransformer {

	private static CustomClassLoader SCL;

	private static final Map<Class<?>, Unloaded<?>> unloadeds = new HashMap<>(700);
	private static final List<String> processedClasses = new ArrayList<String>(700);

	@Override
	public boolean applies() {
		// return Platform.isProduction();
		return true;
	}

	@Override
	public void apply(CustomClassLoader cl, Map<String, List<String>> classes) {

		// Create a new classloader, where bytebuddy-instrumented classes will be
		// finally injected. This classloader will become the Jvm system classloader

		SCL = new CustomClassLoader(false);

		cl

				.getClassesMap().forEach((n, c) -> {

					// System.out.println(c.getName());

					Unloaded<?> unloaded = null;

					try {

						ElementMatcher<MethodDescription> matcher = new ElementMatcher<MethodDescription>() {

							@Override
							public boolean matches(MethodDescription target) {

								/**
								 * 
								 * While we have removed @MethodMeta from <source>, it's possible that a
								 * subclass of <source> but super class of <target> is contained in clPool and
								 * since it was not transformed, the class reference extends the old version of
								 * <source>, hence, it technically still contains the non-declared abstract
								 * method annotated with @Secure. Therefore, we need to skip such method
								 */

								if (target.isAbstract()) {
									return false;
								}

								for (AnnotationDescription a : target.getDeclaredAnnotations()) {
									if (BytecodeTools.annotationApplies(a.getAnnotationType().getTypeName(),
											SecureMethod.class)) {
										return true;
									}
								}
								return false;
							}
						};

						ClassFileLocator locator = new ClassFileLocator() {

							@Override
							public void close() throws IOException {
							}

							@Override
							public Resolution locate(String name) throws IOException {
								return new Resolution() {

									@Override
									public byte[] resolve() {
										return cl.getClassBytes(name);
									}

									@Override
									public boolean isResolved() {
										return true;
									}
								};
							}
						};

						unloaded = new ByteBuddy().with(AnnotationRetention.ENABLED).rebase(c, locator).method(matcher)
								.intercept(
										MethodDelegation.to(MethodInterceptor.class).andThen(SuperMethodCall.INSTANCE))
								.constructor(matcher)
								.intercept(
										MethodDelegation.to(MethodInterceptor.class).andThen(SuperMethodCall.INSTANCE))
								.make();

					} catch (Error | TypeNotPresentException e) {
						// Temporary : probably syntax error
						// Todo: remove
						return;
					}

					unloadeds.put(c, unloaded);
				});

		// Since the class bytes have been loaded using bytebuddy, prune the pooled
		// classloader, since it's no needed anymore
		cl.prune();

		// Recursively load classes into the classloader based on their hierarchy
		for (Entry<Class<?>, Unloaded<?>> e : unloadeds.entrySet()) {
			load(SCL, e.getKey(), e.getValue());
		}

		// Dynamically set the underlying classloader used by the
		// Jvm system class loader
		
		System.out.println(SCL);

		((SystemClassLoaderImpl) ClassLoader.getSystemClassLoader()).setClassLoader(SCL);
	}

	private static void load(CustomClassLoader cl, Class<?> clazz, Unloaded<?> unloaded) {

		// System.out.println("loading " + unloaded.getTypeDescription().getTypeName());

		if (processedClasses.contains(getName(clazz))) {
			return;
		}

		// Recurse super interfaces
		if (clazz.getInterfaces().length > 0) {

			for (Class<?> itf : clazz.getInterfaces()) {
				Unloaded<?> u = getUnloaded(itf);

				if (u != null) {
					load(cl, itf, u);
				}
			}
		}

		// Recurse super classes
		if (clazz.getSuperclass() != null) {
			Unloaded<?> u = getUnloaded(clazz.getSuperclass());

			if (u != null) {
				load(cl, clazz.getSuperclass(), u);
			}
		}

		// Finally, inject into back our custom class loader
		unloaded.load(cl, ClassLoadingStrategy.Default.INJECTION);

		processedClasses.add(getName(clazz));
	}

	private static Unloaded<?> getUnloaded(Class<?> clazz) {
		for (Unloaded<?> u : unloadeds.values()) {
			if (u.getTypeDescription().getTypeName().equals(getName(clazz))) {
				return u;
			}
		}
		return null;
	}

}
