package com.re.paas.api.events;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.re.paas.api.annotations.develop.BlockerTodo;

@Retention(RUNTIME)
@Target(METHOD)

@BlockerTodo("Add localOnly, to indicate that the annotated method only wants to respond to local sourced events")
public @interface Subscribe {
	boolean allowAsyncEvent() default true;	
}
