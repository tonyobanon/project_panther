package com.re.paas.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.re.paas.api.annotations.ProtectionContext;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.runtime.ThreadSecurity;
import com.re.paas.internal.classes.ClasspathScanner;
import com.re.paas.internal.jvmtools.annotations.AnnotationUtil;
import com.re.paas.internal.jvmtools.classloaders.ClassLoaderUtil;
import com.re.paas.internal.jvmtools.classloaders.CustomClassLoader;
import com.re.paas.internal.logging.DefaultLoggerFactory;
import com.re.paas.internal.runtime.ThreadSecurityImpl;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.annotation.AnnotationsWriter;
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

public class CodeSecurity {

	private static final Map<Class<?>, Unloaded<?>> unloadeds = new HashMap<>(700);
	private static final List<String> processedClasses = new ArrayList<String>(700);

	
	/**
	 * This discovers methods with a {@link ProtectionContext}, and copies the
	 * annotation from the super class to the subclass
	 * 
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	public static void scanProtectionContext() throws ClassNotFoundException, NoSuchMethodException, SecurityException {

		System.out.println("Scanning protection context ..");

		ThreadSecurity.setInstance(new ThreadSecurityImpl());
		Singleton.register(LoggerFactory.class, new DefaultLoggerFactory());

		CustomClassLoader cl = new CustomClassLoader(true);

		ArrayList<Class<?>> abstractClasses = newClasspathScanner(
				new CustomClassLoader(false).onFind((clazz, bytes) -> {
					cl.ingest(clazz, bytes);
				})).setAccessAllConstructors(true).setLoadAbstractClasses(true).scanClasses();

		List<Class<?>> classes = cl.getClasses();

		// We need to scan the methods in all abstract class, then we need to scan for
		// subclasses and copy the annotation to the method

		for (Class<?> c : abstractClasses) {

			if (!Modifier.isAbstract(c.getModifiers())) {
				continue;
			}

			Method[] methodsArray = c.getDeclaredMethods();

			// Scan through all methods, to see if there's any abstract method(s) that
			// contains ProtectionContext.class
			List<Method> methods = new ArrayList<>();

			for (Method m : methodsArray) {

				// System.err.println(c.getName() + "#" + m.toString());

				if ((!Modifier.isAbstract(m.getModifiers()))
						|| !AnnotationUtil.hasAnnotation(m, ProtectionContext.class)) {
					continue;
				}

				// System.out.println(c.getName() + "#" + m.toString());

				methods.add(m);
			}

			if (methods.isEmpty()) {
				continue;
			}

			ClassPool cp = ClassPool.getDefault();

			ClassPool.doPruning = true;

			try {

				CtClass source = cp.get(c.getName());

				ProtectionDomain pd = c.getProtectionDomain();

				// Iterate all subclasses to discover methods that the annotation
				// should be copied to

				for (Class<?> impl : classes) {

					if (!c.isAssignableFrom(impl) || c.equals(impl)) {
						continue;
					}

					try {

						CtClass target = cp.get(impl.getName());

						// cp.makePackage(cp.getClassLoader(), c.getPackageName());
						// cp.makePackage(cp.getClassLoader(), impl.getPackageName());

						// We want to start transforming the methods, so we need to loop through

						// This is a flag that indicates that at least one method was transformed,
						// hence <target> needs to be converted to a class and re-ingested

						boolean transformed = false;

						for (Method m : methods) {

							// Here, we are checking if the subclass declared the method

							try {
								m = impl.getDeclaredMethod(m.getName(), m.getParameterTypes());
							} catch (NoSuchMethodException e) {
								// The subclass does not override the super method
								continue;
							}

							copyAnnotation(target, source, m, ProtectionContext.class);
							transformed = true;
						}

						if (transformed) {

							// Ingest the transformed class back into the classloader pool
							cl.ingest(target.getName(), target.toBytecode(), pd);
						}

					} catch (NotFoundException | BadBytecode | CannotCompileException | IOException e) {
						throw new RuntimeException(e);
					}

				}

				// Since the annotations have been copied to subclasses, remove abstract
				// method(s) from <source>

				for (Method m : methods) {
					removeAnnotation(source, m, ProtectionContext.class);
				}

				// Ingest the transformed class back into the classloader pool
				cl.ingest(source.getName(), source.toBytecode(), pd);

			} catch (NotFoundException | CannotCompileException | BadBytecode | IOException e) {
				throw new RuntimeException(e);
			}

		}

		// Create a new classloader, where bytebuddy-instrumented classes will be
		// finally
		// injected. This classloader will become the Jvm system classloader

		CustomClassLoader scl = new CustomClassLoader(false);

		cl

				.getClassesMap().forEach((n, c) -> {

					Unloaded<?> unloaded = null;

					try {

						ElementMatcher<MethodDescription> matcher = new ElementMatcher<MethodDescription>() {

							@Override
							public boolean matches(MethodDescription target) {

								// While we have removed @ProtectionContext from <source>, it's possible that a
								// subclass of <source> but super class of <target> is contained in clPool
								// and since it was not transformed, the class reference extends the old version
								// of <source>, hence, it technically still contains the non-declared abstract
								// method annotated with @ProtectionContext. Therefore, we need to skip such
								// method

								if (target.isAbstract()) {
									return false;
								}

								for (AnnotationDescription a : target.getDeclaredAnnotations()) {
									if (a.getAnnotationType().getTypeName().equals(ProtectionContext.class.getName())) {
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

						unloaded = new ByteBuddy().with(AnnotationRetention.ENABLED).rebase(c, locator)
								.method(matcher)
								.intercept(MethodDelegation.to(Interceptor.class).andThen(SuperMethodCall.INSTANCE))
								.constructor(matcher)
								.intercept(MethodDelegation.to(Interceptor.class).andThen(SuperMethodCall.INSTANCE))
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

		// Since, we are now ready to load classes into the system
		// classloader, we need to reset the ThreadSecurity instance
		// because the current instance uses the former classloader

		for (Entry<Class<?>, Unloaded<?>> e : unloadeds.entrySet()) {
			load(scl, e.getKey(), e.getValue());
		}

		// Dynamically set the Jvm system class loader
		// As a repercussion, future calls to (~ ClassLoader.getSystemClassLoader() 
		// instanceof CustomClassLoader) will always return false
		// This is because the Jvm does not allow dynamic casts for the system
		// class loader. What the Jvm does not know is that we changed it :)

		ClassLoaderUtil.setSystemClassLoder(scl);
	}

	private static void load(ClassLoader cl, Class<?> clazz, Unloaded<?> unloaded) {

		// System.out.println("loading " + unloaded.getTypeDescription().getTypeName());

		if (processedClasses.contains(clazz.getName())) {
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

		processedClasses.add(clazz.getName());
	}

	private static Unloaded<?> getUnloaded(Class<?> clazz) {
		for (Unloaded<?> u : unloadeds.values()) {
			if (u.getTypeDescription().getTypeName().equals(clazz.getName())) {
				return u;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param target The target class to be transformed
	 * @param source The source class from where we read the annotation
	 * @param method The method from where we read the annotation
	 * @throws NotFoundException
	 * @throws BadBytecode
	 * @throws CannotCompileException
	 * @throws IOException
	 * @throws Exception
	 */
	public static void copyAnnotation(CtClass target, CtClass source, Method method,
			Class<? extends Annotation> annotationType)
			throws NotFoundException, BadBytecode, CannotCompileException, IOException {

		// If this method is already annotated with annotationType, remove
		for (Annotation a : method.getDeclaredAnnotations()) {
			if (a.annotationType().getName().equals(annotationType.getName())) {
				removeAnnotation(target, method, a.annotationType());
			}
		}

		// Create an array of CtClass instances for the method param types
		Class<?>[] paramsTypes = method.getParameterTypes();
		List<CtClass> paramsTypesCT = new ArrayList<>();

		for (Class<?> cl : paramsTypes) {
			paramsTypesCT.add(source.getClassPool().get(cl.getName()));
		}

		// Get annotation bytes from the source method
		CtMethod sourceMethod = source.getDeclaredMethod(method.getName(),
				paramsTypesCT.toArray(new CtClass[paramsTypesCT.size()]));

		CtMethod targetMethod = target.getDeclaredMethod(method.getName(),
				paramsTypesCT.toArray(new CtClass[paramsTypesCT.size()]));

		AnnotationsAttribute sourceAnnotationsAttr = new AnnotationsAttribute(source.getClassFile().getConstPool(),
				AnnotationsAttribute.visibleTag, sourceMethod.getAttribute(AnnotationsAttribute.visibleTag));

		AnnotationsAttribute targetAnnotationsAttr = new AnnotationsAttribute(target.getClassFile().getConstPool(),
				AnnotationsAttribute.visibleTag, targetMethod.getAttribute(AnnotationsAttribute.visibleTag));

		for (javassist.bytecode.annotation.Annotation a : sourceAnnotationsAttr.getAnnotations()) {

			if (a.getTypeName().equals(annotationType.getName())) {

				ByteArrayOutputStream output = new ByteArrayOutputStream();
				int numAnnotations = 1;

				if (targetAnnotationsAttr.get() != null) {
					numAnnotations += targetAnnotationsAttr.numAnnotations();
				}

				AnnotationsWriter writer = new AnnotationsWriter(output, target.getClassFile().getConstPool());

				writer.numAnnotations(numAnnotations);

				if (targetAnnotationsAttr.get() != null) {
					for (javassist.bytecode.annotation.Annotation a2 : targetAnnotationsAttr.getAnnotations()) {
						a2.write(writer);
					}
				}

				writer.annotation("L" + annotationType.getName().replace(".", "/") + ";",
						a.getMemberNames() != null ? a.getMemberNames().size() : 0);

				if (a.getMemberNames() != null) {
					for (String name : a.getMemberNames()) {
						writer.memberValuePair(name); // element_value_pair
						a.getMemberValue(name).write(writer);
					}
				}

				writer.close();
				byte[] attribute_info = output.toByteArray();
				targetAnnotationsAttr.set(attribute_info);
			}
		}

		// Add annotation to method info
		targetMethod.getMethodInfo().addAttribute(targetAnnotationsAttr);
	}

	public static void removeAnnotation(CtClass clazz, Method method, Class<? extends Annotation> annotationType)
			throws NotFoundException, BadBytecode, CannotCompileException {

		// Create an array of CtClass instances for the method param types
		Class<?>[] paramsTypes = method.getParameterTypes();
		List<CtClass> paramsTypesCT = new ArrayList<>();

		for (Class<?> cl : paramsTypes) {
			paramsTypesCT.add(clazz.getClassPool().get(cl.getName()));
		}

		// Get annotation bytes from the source method
		CtMethod sourceMethod = clazz.getDeclaredMethod(method.getName(),
				paramsTypesCT.toArray(new CtClass[paramsTypesCT.size()]));

		// Removes the specified annotation type from sourceAnnotationsAttr

		AnnotationsAttribute sourceAnnotationsAttr = new AnnotationsAttribute(clazz.getClassFile().getConstPool(),
				AnnotationsAttribute.visibleTag, sourceMethod.getAttribute(AnnotationsAttribute.visibleTag));

		sourceAnnotationsAttr.removeAnnotation(annotationType.getName());

		// Add annotation to method info
		sourceMethod.getMethodInfo().addAttribute(sourceAnnotationsAttr);
	}

	private static ClasspathScanner<Object> newClasspathScanner(ClassLoader cl) {
		return (ClasspathScanner<Object>) newClasspathScanner(cl, Object.class);
	}

	private static <T> ClasspathScanner<T> newClasspathScanner(ClassLoader cl, Class<T> type) {
		return new ClasspathScanner<>("", type).setClassLoader(cl)
				.setLoadAbstractClasses(true);
	}

}
