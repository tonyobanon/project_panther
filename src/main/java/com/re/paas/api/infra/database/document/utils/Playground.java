package com.re.paas.api.infra.database.document.utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ParameterizedClass;
import com.re.paas.api.utils.ClassUtils;

public class Playground {
	
	private Set<?> h;
	
	public static void main(String[] args) {

		Field field = null;
		try {
			field = Playground.class.getDeclaredField("h");
		} catch (NoSuchFieldException e) {
			Exceptions.throwRuntime(e);
		}

		field.setAccessible(true);
		
		ParameterizedClass c = ClassUtils.getParameterizedClass(ClassLoader.getSystemClassLoader(), field.getGenericType().getTypeName());
		
		System.out.println(c.getType());
		System.out.println(c.getGenericTypes().get(0).getType());
		
	}
}
