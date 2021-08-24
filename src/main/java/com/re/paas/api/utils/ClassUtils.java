package com.re.paas.api.utils;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ParameterizedClass;
import com.re.paas.api.runtime.RuntimeIdentity;
import com.re.paas.api.runtime.SystemClassLoader;
import com.re.paas.api.runtime.spi.AppProvisioner;

public class ClassUtils<T> {

	private static final Pattern simpleName = Pattern
			.compile("((([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*)|\\Q?\\E)");

	private static final Pattern className = Pattern.compile(createClassNamePattern(
			// The maximum depth for which we want to recognize match generic types in the
			// class name
			10));

	private static final Pattern GENERIC_TYPE_PATTERN = Pattern
			.compile("((?<=\\Q<\\E)" + className + "(\\s*\\Q,\\E\\s*" + className + ")*" + "(?=\\Q>\\E\\z){1}){1}");

	private static String replaceString(Integer i, String word, String subString) {
		return i == 0 ? word
				: word.replace(
						subString,
						ClassUtils.replaceString(i - 1, word, subString)
				);
	}

	private static String createClassNamePattern(Integer maxDepth) {

		// This can be any string, as long it does not conflict with any other sequence
		// in the regex
		final String genericTypeMarker = Utils.randomString(3);
		
		// System.out.println(genericTypeMarker);

		final String classNamePattern = simpleName + "(\\Q<\\E" + genericTypeMarker + "(\\s*\\Q,\\E\\s*"
				+ genericTypeMarker + ")*" + "\\Q>\\E){0,1}";

		String result = replaceString(maxDepth, classNamePattern, genericTypeMarker);

		// System.out.println(result);
		
		result = result.replace(genericTypeMarker, simpleName.toString());

		// System.out.println(result);

		return result;
	}

	public static boolean isMethodOverrriden(final Method myMethod) {
		Class<?> declaringClass = myMethod.getDeclaringClass();
		if (declaringClass.equals(Object.class)) {
			return false;
		}
		try {
			declaringClass.getSuperclass().getMethod(myMethod.getName(), myMethod.getParameterTypes());
			return true;
		} catch (NoSuchMethodException e) {
			for (Class<?> iface : declaringClass.getInterfaces()) {
				try {
					iface.getMethod(myMethod.getName(), myMethod.getParameterTypes());
					return true;
				} catch (NoSuchMethodException ignored) {

				}
			}
			return false;
		}
	}

	public static Boolean isSerializable(Object o) {
		return Serializable.class.isAssignableFrom(o.getClass());
	}

	public static InputStream getResourceAsStream(Class<?> claz, String name) {

		/**
		 * remove leading file separator so path will work with classes in a JAR file
		 */
		while (name.startsWith(File.separator)) {
			name = name.substring(1);
		}

		ClassLoader classLoader = claz.getClassLoader();

		return classLoader.getResourceAsStream(name);

	}

	public static ParameterizedClass getParameterizedClass(ClassLoader cl, Type type) {
		return getParameterizedClass(cl, type.getTypeName());
	}

	/**
	 * 
	 * @param cl
	 * @param typeName
	 * @return Class | List<><Class>
	 */
	@BlockerTodo("Use tokenization to parse type tokens, instead of regex")
	@Deprecated(forRemoval = true)
	public static ParameterizedClass getParameterizedClass(ClassLoader cl, String typeName) {

		Matcher m = simpleName.matcher(typeName);
		m.find();

		String className = m.group();

		// System.out.println("\n" + className);
		// System.out.println(typeName);

		ParameterizedClass result = null;

		try {

			Class<?> c = cl.loadClass(className);
			result = new ParameterizedClass(c);

		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Unknown class: " + className);
		}

		Matcher m1 = GENERIC_TYPE_PATTERN.matcher(typeName);

		boolean b = m1.find();

		// System.out.println(b);

		if (b) {

			String classes = m1.group();

			// System.out.println(classes);

			Matcher m2 = ClassUtils.className.matcher(classes);

			while (m2.find()) {
				result.addGenericType(getParameterizedClass(cl, m2.group()));
			}
		}

		return result;
	}

	public static <T> T createInstance(Class<T> clazz) {
		try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	@SuppressWarnings("unused")
	private static Class<?> getSuperClass(Class<?> clazz) {

		while (clazz.getSuperclass() != null && !(clazz.getSuperclass().equals(Object.class))) {
			clazz = clazz.getSuperclass();
		}

		return clazz;
	}

	/**
	 * This function generates a name that can be used to identify this class
	 * uniquely accross the platform
	 * 
	 * @param clazz
	 * @return
	 */
	public static String toString(Class<?> clazz) {

		ClassLoader cl = clazz.getClassLoader();
		String appId = null;

		if (cl instanceof AppClassLoader) {
			appId = ((AppClassLoader) cl).getAppId();
		} else {
			appId = AppProvisioner.DEFAULT_APP_ID;
		}

		return appId + "#" + clazz.getName();
	}

	public static Class<?> getSuperclass(Class<?> clazz, Class<?> superclass) {

		while ((clazz = clazz.getSuperclass()) != null) {

			if (clazz.equals(superclass)) {
				return clazz;
			}
		}

		return null;
	}

	/**
	 * This function determines whether the current context is allowed to access the
	 * specified class
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isAccessible(Class<?> clazz) {

		SystemClassLoader scl = (SystemClassLoader) ClassLoader.getSystemClassLoader();

		// If this is a platform class, allow access
		if (scl.isPlatformClass(clazz)) {
			return true;
		}

		// Since it's not, allow if the app is the owner

		AppClassLoader cl = (AppClassLoader) RuntimeIdentity.class.getClassLoader();
		return cl == clazz.getClassLoader();
	}

	public static Object getFieldValue(Class<?> clazz, String name) {
		return getFieldValue(clazz, null, name);
	}

	public static Object getFieldValue(Class<?> clazz, Object instance, String name) {
		try {

			Field f = clazz.getDeclaredField(name);

			f.setAccessible(true);

			return f.get(instance);

		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	/**
	 * This implementation may change in the future
	 * 
	 * @param class1
	 * @param class2
	 * @return
	 */
	@BlockerTodo("Verify that this works in a distributed scenario")
	public static boolean equals(Class<?> class1, Class<?> class2) {
		return class1.equals(class2);
	}

	/**
	 * This classes attempts to reshuffle the given list, such that higher-depth
	 * subclasses appear top in the returned list
	 * 
	 * @param classes
	 * @return
	 */
	public static <T> ArrayList<Class<? extends T>> reshuffleByDepth(Class<T> classType,
			Collection<Class<? extends T>> classes, boolean highestFirst) {

		Map<Class<? extends T>, Integer> classDepths = new HashMap<>();

		classes.forEach(c -> {
			classDepths.put(c, getInheritanceDepth(0, classType, c));
		});

		LinkedList<Class<? extends T>> sortedMap = new LinkedList<>();

		Comparator<? super Entry<Class<? extends T>, Integer>> comparator = highestFirst
				? Map.Entry.comparingByValue(Comparator.reverseOrder())
				: Map.Entry.comparingByValue();

		classDepths.entrySet().stream().sorted(comparator).forEachOrdered(x -> sortedMap.add(x.getKey()));

		return new ArrayList<>(sortedMap);
	}

	@SuppressWarnings("unchecked")
	public static <T> int getInheritanceDepth(int currentDepth, Class<T> classType, Class<? extends T> clazz) {

		boolean isDirectChild = isDirectChild(classType, clazz);

		if (isDirectChild) {
			return currentDepth;
		}

		Class<? extends T> parent = (Class<? extends T>) clazz.getSuperclass();

		if (parent == null) {
			return currentDepth;
		}

		return getInheritanceDepth(currentDepth++, classType, parent);
	}

	public static <T> boolean isDirectChild(Class<T> classType, Class<? extends T> clazz) {

		if (classType.isInterface()) {
			if (Arrays.asList(clazz.getInterfaces()).contains(classType)) {
				return true;
			}
		} else {

			if (clazz.getSuperclass() != null && clazz.getSuperclass().equals(classType)
					&& !clazz.getName().equals(classType.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This consumes the class and its ancestor up until the specified super class
	 * (exclusive)
	 * 
	 * @param clazz
	 * @param superClass
	 * @param consumer
	 */
	public static <T> void forEachInTree(Class<? extends T> clazz, Class<T> superClass,
			Consumer<Class<? extends T>> consumer) {

		if (!superClass.isAssignableFrom(clazz)) {
			Exceptions.throwRuntime(superClass.getName() + " is not assignable from " + clazz.getName());
		}

		Class<?> c = clazz;

		do {
			@SuppressWarnings("unchecked")
			Class<? extends T> cl = (Class<? extends T>) c;
			consumer.accept(cl);
		} while ((!c.getSuperclass().equals(Object.class)) && (!c.getSuperclass().equals(superClass))
				&& (c = c.getSuperclass()) != null);

	}

	public static Class<?> load(ClassLoader cl, String className) {
		return loadAll(cl, Arrays.asList(className)).get(0);
	}

	public static List<Class<?>> loadAll(ClassLoader cl, List<String> classNames) {

		List<Class<?>> result = new ArrayList<>(classNames.size());

		classNames.stream().map(className -> {

			Class<?> c = null;

			try {
				c = cl.loadClass(className);
			} catch (ClassNotFoundException e) {
				Exceptions.throwRuntime(e);
			}

			return c;
		}).collect(Collectors.toList());

		return result;
	}

}
