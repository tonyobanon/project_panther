package com.re.paas.internal.jvmtools.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;

public class AnnotationUtil {

	public static Annotation getAnnotation(Executable m, Class<? extends Annotation> type) {
		Annotation[] annotations = m.getAnnotations();
		if(annotations.length > 0) {
			for (Annotation a : annotations) {
				if(a.toString().startsWith("@" + type.getName())) {
					return a;
				}
			}
		}
		return null;
	}
	
	public static boolean hasAnnotation(Method m, Class<? extends Annotation> type) {
		return getAnnotation(m, type) != null;
	}
	
}
