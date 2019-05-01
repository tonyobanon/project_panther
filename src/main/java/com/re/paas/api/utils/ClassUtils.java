package com.re.paas.api.utils;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.runtime.ClassLoaderSecurity;
import com.re.paas.internal.runtime.spi.AppProvisioner;

public class ClassUtils<T> {

	private static final Pattern DOT_PATTERN = Pattern.compile(Pattern.quote("."));
	private static final Pattern HASH_PATTERN = Pattern.compile(Pattern.quote("#"));

	private static final String classNamePattern = "([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*";
	private static final Pattern GENERIC_TYPE_PATTERN = Pattern
			.compile("((?<=\\Q<\\E)" + classNamePattern + "(\\Q, \\E" + classNamePattern + ")*" + "(?=\\Q>\\E\\z)){1}");

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

	public static <T> T createInstance(String name) {
		@SuppressWarnings("unchecked")
		Class<? extends T> clazz = (Class<? extends T>) forName(name);
		T o = createInstance(clazz);

		return o;
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

	@BlockerTodo("Add support for caching")
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

	public static InputStream getResourceAsStream(Class<?> claz, String name) {

		/**
		 * remove leading slash so path will work with classes in a JAR file
		 */
		while (name.startsWith("/")) {
			name = name.substring(1);
		}

		ClassLoader classLoader = claz.getClassLoader();

		return classLoader.getResourceAsStream(name);

	}

	@Todo("Add support for multi-depth generic hierarchies")
	@BlockerTodo("Split using proper regex not ', '")
	/**
	 * This method does not support multi-depth generic hierarchies
	 */
	public static List<Class<?>> getGenericRefs(ClassLoader cl, Type t) {
		List<Class<?>> result = Lists.newArrayList();

		String typeName = t.getTypeName();

		Matcher m = GENERIC_TYPE_PATTERN.matcher(typeName);

		boolean b = m.find();

		if (b) {
			String classes = m.group();

			for (String o : classes.split(", ")) {
				try {
					result.add(cl.loadClass(o));
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return result;
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

		while (!equals(clazz, abstractParent) && (clazz.getSuperclass() != null)) {

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

	@SuppressWarnings("unused")
	private static Class<?> getSuperClass(Class<?> clazz) {

		while (clazz.getSuperclass() != null && !(clazz.getSuperclass().equals(Object.class))) {
			clazz = clazz.getSuperclass();
		}

		return clazz;
	}

	public static String getPackageName(String className) {
		List<String> parts = new ArrayList<>(Splitter.on(DOT_PATTERN).splitToList(className));
		parts.remove(parts.size() - 1);
		return Joiner.on(".").join(parts);
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

	/**
	 * This function should be used by names that were generated using
	 * {@link ClassUtils#toString(Class)}
	 * 
	 * @param name
	 * @return
	 */
	public static <T> Class<? extends T> forName(String name) {

		String[] parts = HASH_PATTERN.split(name);

		String appId = parts[0];
		String className = parts[1];

		ClassLoader cl = null;

		if (AppProvisioner.DEFAULT_APP_ID.equals(appId)) {
			cl = ClassLoader.getSystemClassLoader();
		} else {
			cl = AppProvisioner.get().getClassloader(appId);
		}

		return forName(className, cl);
	}

	/**
	 * This function determines whether the current context is allowed to access the
	 * specified class
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isAccessible(Class<?> clazz) {

		if (ClassLoaderSecurity.hasTrust()) {
			return true;
		}

		// If this is a platform class, allow access
		if (clazz.getClassLoader().equals(ClassLoader.getSystemClassLoader())) {
			return true;
		}

		// Since it's not, allow if the app is the owner

		AppClassLoader cl = (AppClassLoader) ClassLoaderSecurity.class.getClassLoader();
		return cl == clazz.getClassLoader();
	}

	/**
	 * This method should only be used for SPI classes
	 * @param clazz
	 * @return
	 */
	public static String getAppId(Class<?> clazz) {
		ClassLoader cl = clazz.getClassLoader();

		if (cl instanceof AppClassLoader) {
			return ((AppClassLoader) cl).getAppId();
		} else {
			return AppProvisioner.DEFAULT_APP_ID;
		}
	}

	public static boolean isTrusted(Class<?> c) {
		boolean isTrusted = !(c.getClassLoader() instanceof AppClassLoader);
		return isTrusted;
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

	public static void updateField(Class<?> clazz, String name, Object value) {
		updateField(clazz, null, name, value);
	}

	public static void updateField(Class<?> clazz, Object instance, String name, Object value) {

		try {

			Field f = clazz.getDeclaredField(name);
			f.setAccessible(true);

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);

			f.set(instance, value);

		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			Exceptions.throwRuntime(e);
		}
	}

	/**
	 * This implementation may change in the future
	 * 
	 * @param class1
	 * @param class2
	 * @return
	 */
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
}
