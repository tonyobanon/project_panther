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

import com.re.paas.api.runtime.ConcreteIntrinsic;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.instrumentation.BytecodeTools;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;

public class ConcreteIntrinsicTransformer extends ClassTransformer {

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
			 * contains ConcreteIntrinsic.class
			 */
			List<Method> methods = new ArrayList<>();

			for (Method m : methodsArray) {

				if (Modifier.isStatic(m.getModifiers()) || Modifier.isAbstract(m.getModifiers())
						|| Modifier.isFinal(m.getModifiers())
						|| (!BytecodeTools.hasAnnotation(m, ConcreteIntrinsic.class))) {
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

				CtClass source = cp.get(ClassUtils.getName(abstractClass));

				ProtectionDomain pd = abstractClass.getProtectionDomain();

				for (Class<?> impl : implClasses) {

					if (Modifier.isAbstract(impl.getModifiers())) {
						continue;
					}

					try {

						CtClass target = cp.get(ClassUtils.getName(impl));

						for (Method m : methods) {

							try {

								if (target.isFrozen()) {
									target.defrost();
								}

								Method m2 = impl.getDeclaredMethod(m.getName(), m.getParameterTypes());
								if (m2 != null) {

									// If subclass overrides method, delete the overridden version
									BytecodeTools.removeMethod(target, m2);
								}

							} catch (NoSuchMethodException ex) {
							}

							@SuppressWarnings("unchecked")
							Class<? extends Annotation>[] annotationTypes = new Class[] { SecureMethod.class };
							
							BytecodeTools.copyMethod(target, source, m, annotationTypes);
						}

						// Ingest the transformed class back into the classloader pool
						cl.ingest(target.getName(), target.toBytecode(), pd);

					} catch (NotFoundException | BadBytecode | CannotCompileException ex) {
						throw new RuntimeException(ex);
					}

				}

				for (Method m : methods) {

					RuntimeTransformers.LOG.debug("Making " + m.getName() + " an abstract method");
					BytecodeTools.toAbstractMethod(source, m);
				}

				// Ingest the transformed class back into the classloader pool
				cl.ingest(source.getName(), source.toBytecode(), pd);

			} catch (ClassNotFoundException | NotFoundException | BadBytecode | CannotCompileException
					| IOException ex) {
				throw new RuntimeException(ex);
			}

		}
	}

}
