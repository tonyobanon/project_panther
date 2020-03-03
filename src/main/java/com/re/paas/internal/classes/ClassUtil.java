package com.re.paas.internal.classes;

import java.lang.StackWalker.StackFrame;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.JvmConstants;
import com.re.paas.api.runtime.ClassLoaders;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.api.utils.ClassUtils;

@BlockerTodo("Replace usage of guava, then merge with it's API counterpart")
public class ClassUtil {

	private static final Pattern DOT_PATTERN = Pattern.compile(Pattern.quote("."));
	private static final Pattern HASH_PATTERN = Pattern.compile(Pattern.quote("#"));

	public static <T> T createInstance(String name) {
		@SuppressWarnings("unchecked")
		Class<? extends T> clazz = (Class<? extends T>) forName(name);
		T o = createInstance(clazz);

		return o;
	}

	/**
	 * This function should be used by names that were generated using
	 * {@link ClassUtil#toString(Class)}
	 * 
	 * @param name
	 * @return
	 */
	public static <T> Class<? extends T> forName(String name) {

		String[] parts = HASH_PATTERN.split(name);

		if (parts.length == 1) {
			return forName(name, ClassLoaders.getClassLoader());
		}

		String appId = parts[0];
		String className = parts[1];

		ClassLoader cl = ClassLoaders.getClassLoader(appId);

		return forName(className, cl);
	}

	/**
	 * Invokes a callable
	 * 
	 * @param name
	 * @param cl
	 * @return
	 */
	public static void call(Class<?> target, ClassLoader cl) {
		Class<? extends Callable<?>> clazz = forName(target.getName(), cl);
		try {
			createInstance(clazz).call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> Class<? extends T> forName(String name, ClassLoader cl) {

		try {
			@SuppressWarnings("unchecked")
			Class<? extends T> o = (Class<? extends T>) cl.loadClass(name);
			return o;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@BlockerTodo("Add support for caching")
	@SecureMethod
	public static <T> T createInstance(Class<T> clazz) {

		try {

			return clazz.getDeclaredConstructor().newInstance();

		} catch (InstantiationException | InvocationTargetException | NoSuchMethodException e) {

			Exceptions.throwRuntime(e);
			return null;

		} catch (IllegalAccessException e) {

			// Try again, but this time, temporarily make the no-arg constructors accessible

			Constructor<?> modified = null;

			for (Constructor<?> c : clazz.getDeclaredConstructors()) {
				if (c.getParameterCount() == 0) {
					c.setAccessible(true);
					modified = c;
					break;
				}
			}

			try {

				T o = null;
				if (modified != null) {
					o = clazz.getDeclaredConstructor().newInstance();
					modified.setAccessible(false);
				}

				return o;

			} catch (Exception ex) {
				Exceptions.throwRuntime(e);
				return null;
			}

		}
	}

	public static List<Field> getInheritedFields(Class<?> clazz, Class<?> abstractParent) {
		return getInheritedFields0(clazz.getSuperclass(), abstractParent, null);
	}

	public static List<Field> getInheritedFields(Class<?> clazz, Class<?> abstractParent,
			Consumer<Field> fieldConsumer) {
		return getInheritedFields0(clazz.getSuperclass(), abstractParent, fieldConsumer);
	}

	private static List<Field> getInheritedFields0(Class<?> clazz, Class<?> abstractParent,
			Consumer<Field> fieldConsumer) {

		assert abstractParent.isAssignableFrom(clazz);

		List<Field> result = Lists.newArrayList();

		while (!ClassUtils.equals(clazz, abstractParent) && (clazz.getSuperclass() != null)) {

			for (Field f : clazz.getDeclaredFields()) {

				fieldConsumer.accept(f);

				int mod = f.getModifiers();
				if (Modifier.isProtected(mod) && (!Modifier.isFinal(mod))) {
					result.add(f);
				}
			}

			clazz = clazz.getSuperclass();
		}

		return result;
	}

	@BlockerTodo("Factor in nested classes as well")
	public static String getPackageName(String className) {
		List<String> parts = new ArrayList<>(Splitter.on(DOT_PATTERN).splitToList(className));
		parts.remove(parts.size() - 1);
		return Joiner.on(".").join(parts);
	}

	@BlockerTodo("Perform investigations here to determine how all syncthentic methods look like.. Because "
			+ "1. This only take lambda meta-methods into consideration, there may be other scenarios"
			+ "2. As the Jvm evolves, this is subject to change")

	public static Boolean isFrameSynthetic(StackFrame frame) {
		return frame.getMethodName().startsWith(JvmConstants.LAMBDA_SYNTHETIC_METHOD_PREFIX);
	}

}
