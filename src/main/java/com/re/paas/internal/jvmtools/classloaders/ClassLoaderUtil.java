package com.re.paas.internal.jvmtools.classloaders;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Vector;

public class ClassLoaderUtil {

	public static List<Class<?>> getLoadedClases(ClassLoader cl) {
		try {
			Field f = ClassLoader.class.getDeclaredField("classes");
			f.setAccessible(true);
			@SuppressWarnings("unchecked")
			Vector<Class<?>> classes = (Vector<Class<?>>) f.get(cl);
			return classes;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setSystemClassLoder(ClassLoader cl) {
		try {
			Field scl = ClassLoader.class.getDeclaredField("scl");
			scl.setAccessible(true);
			scl.set(null, cl);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
