package com.re.paas.api.runtime.spi;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)

/**
 * Annotation to indicate that a delegate depends on another delegate in its init() method
 * */
public @interface DelegateSpec {
	SpiType[] dependencies() default {};
}
