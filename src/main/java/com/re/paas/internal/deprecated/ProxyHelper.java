package com.re.paas.internal.deprecated;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.internal.jvmtools.annotations.AnnotationParser;
import com.re.paas.internal.jvmtools.annotations.ClassTools;
import com.re.paas.internal.jvmtools.classloaders.ClassLoaderUtil;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;

public class ProxyHelper {

	/**
	 * 
	 * Todo: Boolean values are problematic in the enum
	 * Todo: Add support for Repeatable annotation
	 * 
	 * @param clazz
	 * @param annotation which is a proxy object
	 * @return
	 */
	public static <T> T generateAnnotation(Class<T> clazz, Annotation annotation) {

		if (!Proxy.isProxyClass(annotation.getClass())) {
			@SuppressWarnings("unchecked")
			T r = (T) annotation;
			return r;
		}

		Map<String, Object> values = AnnotationParser.getAnnotationValues(annotation, true);

		values.values().forEach(v -> {
			if (v == null) {
				Exceptions.throwRuntime("All methods should have a default value");
			}
		});

		try {

			ClassPool cp = new ClassPool(true);
			CtClass ct = cp.makeClass(clazz.getName());

			Method[] methods = annotation.annotationType().getDeclaredMethods();
			for (Method method : methods) {

				Object value = values.get(method.getName());

				String returnType = getReturnType(value);
				String returnValue = getReturnValue(value);

				ct.addMethod(CtNewMethod.make(
						"public static " + returnType + " " + method.getName() + "() { return " + returnValue + " ;}",
						ct));
			}
			
			ct.addConstructor(CtNewConstructor.defaultConstructor(ct));
			
			ClassLoader cl = ClassTools.class.getClassLoader();
			
			List<Class<?>> classes = ClassLoaderUtil.getLoadedClases(cl);
			
			classes.removeIf(c -> c.getName().equals(clazz.getName()));
			classes.add(clazz);
			
			Class<?> c = ct.toClass(cl, null);
			
			@SuppressWarnings("unchecked")
			T t = (T) c.getConstructor().newInstance();
			return t;

		} catch (CannotCompileException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	private static String getReturnValue(Object value) {
		String returnValue = null;

		if (value instanceof Boolean || value instanceof Number) {
			returnValue = value.toString();
		} else if (value instanceof String) {
			returnValue = "\"" + value + "\"";
		} else if (value instanceof Class<?>) {
			returnValue = ((Class<?>) value).getName().replace("$", ".") + ".class";
		} else if (value instanceof Enum<?>) {
			returnValue = value.getClass().getName().replace("$", ".") + "." + value;
		} else if (value.getClass().isArray()) {

			String arrayType = value.getClass().getName().replaceFirst("\\Q[L\\E", "").replace(";", "");

			List<String> returnValues = new ArrayList<>();
			for (Object o : (Object[]) value) {
				returnValues.add(getReturnValue(o));
			}
			returnValue = "new " + arrayType + "[] {" + Joiner.on(",").join(returnValues) + "}";
		}
		return returnValue;
	}

	private static String getReturnType(Object value) {
		String returnType = null;

		if (value instanceof Boolean || value instanceof Number) {
			returnType = value.getClass().getName();
		} else if (value instanceof String) {
			returnType = value.getClass().getName();
		} else if (value instanceof Class<?>) {
			returnType = value.getClass().getName();
		} else if (value instanceof Enum<?>) {
			returnType = value.getClass().getName().replace("$", ".");
			;
		} else if (value.getClass().isArray()) {

			String arrayType = value.getClass().getName().replaceFirst("\\Q[L\\E", "").replace(";", "");

			returnType = arrayType + "[]";
		}
		return returnType;
	}
}
