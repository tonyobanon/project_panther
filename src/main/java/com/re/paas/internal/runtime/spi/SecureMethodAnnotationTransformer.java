package com.re.paas.internal.runtime.spi;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.instrumentation.BytecodeTools;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;

import static com.re.paas.api.utils.ClassUtils.getName;

public class SecureMethodAnnotationTransformer extends ClassTransformer {

	@Override
	public boolean applies() {
		// return Platform.isProduction();
		return true;
	}
	
	/**
	 * If this is true, then non-static non-final concrete methods that is annotated
	 * with @SecureMethod, and a member of an abstract class will not be automatically
	 * made abstract, hence allowing bytebuddy to instrument it as well as it's
	 * overridden versions in sub-classes
	 */
	private static final boolean allowMultiMethodInstrumentation = true;

	@Override
	public void apply(CustomClassLoader cl, Map<String, List<String>> classes) {

		for (Entry<String, List<String>> e : classes.entrySet()) {

			Class<?> abstractClass = ClassUtils.load(cl, e.getKey());
		
			List<Class<?>> implClasses = ClassUtils.loadAll(cl, e.getValue());

			if (!Modifier.isAbstract(abstractClass.getModifiers())) {
				continue;
			}

			Method[] methodsArray = abstractClass.getDeclaredMethods();

			/**
			 * Scan through all methods, to see if there's any abstract method(s) that
			 * contains MethodMeta.class
			 */
			List<Method> methods = new ArrayList<>();

			for (Method m : methodsArray) {

				if (Modifier.isStatic(m.getModifiers()) || Modifier.isFinal(m.getModifiers())
						|| (!BytecodeTools.hasAnnotation(m, SecureMethod.class))) {
					continue;
				}

				methods.add(m);
			}

			if (methods.isEmpty()) {
				continue;
			}

			ClassPool cp = ClassPool.getDefault();

			ClassPool.doPruning = true;

			try {

				CtClass source = cp.get(getName(abstractClass));

				ProtectionDomain pd = abstractClass.getProtectionDomain();

				// Iterate all subclasses to discover methods that the annotation
				// should be copied to

				for (Class<?> impl : implClasses) {

					try {

						CtClass target = cp.get(getName(impl));

						// cp.makePackage(cp.getClassLoader(), c.getPackageName());
						// cp.makePackage(cp.getClassLoader(), impl.getPackageName());

						// We want to start transforming the methods, so we need to loop through

						// This is a flag that indicates that at least one method was transformed,
						// hence <target> needs to be converted to a class and re-ingested

						boolean transformed = false;

						for (Method m : methods) {

							try {

								m = impl.getDeclaredMethod(m.getName(), m.getParameterTypes());

								if (!Modifier.isAbstract(m.getModifiers())) {

									if (target.isFrozen()) {
										target.defrost();
									}
									
									@SuppressWarnings("unchecked")
									Class<? extends Annotation>[] annotationTypes = new Class[] {SecureMethod.class};

									BytecodeTools.copyAnnotation(target, source, m, annotationTypes);
									transformed = true;
								}

							} catch (NoSuchMethodException | ClassNotFoundException ex) {
							}

						}

						if (transformed) {

							// Ingest the transformed class back into the classloader pool
							cl.ingest(target.getName(), target.toBytecode(), pd);
						}

					} catch (NotFoundException | BadBytecode | CannotCompileException | IOException ex) {
						throw new RuntimeException(ex);
					}

				}

				for (Method m : methods) {

					if (Modifier.isAbstract(m.getModifiers())) {

						// Remove annotation from abstract method. Note however, that in the Bytebuddy
						// element matcher, we still need to check for abstract methods, due class
						// references in the classloader pool that need to be pruned. See
						// SecureMethodTransformer for more info

						BytecodeTools.removeAnnotation(source, m, SecureMethod.class);

					} else if (!allowMultiMethodInstrumentation) {

						// If this method is a non-static non-final concrete one,
						// that is annotated with @SecureMethod, make abstract

						RuntimeTransformers.LOG.debug("Making " + m.getName() + " an abstract method");
						BytecodeTools.toAbstractMethod(source, m);
					}
				}

				// Ingest the transformed class back into the classloader pool
				cl.ingest(source.getName(), source.toBytecode(), pd);

			} catch (ClassNotFoundException | NotFoundException | BadBytecode | CannotCompileException | IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

}
