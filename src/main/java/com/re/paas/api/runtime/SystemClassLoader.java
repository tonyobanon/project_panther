package com.re.paas.api.runtime;


public interface SystemClassLoader {
	
	Boolean isPlatformClass(Class<?> clazz);
	
}