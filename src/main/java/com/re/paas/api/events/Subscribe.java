package com.re.paas.api.events;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.re.paas.api.tasks.Affinity;

@Retention(RUNTIME)
@Target(METHOD)

public @interface Subscribe {
	
	boolean allowAsyncEvents() default true;	
	
	Affinity affinity() default Affinity.LOCAL;
	
}
