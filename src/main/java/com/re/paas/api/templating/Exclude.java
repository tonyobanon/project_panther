package com.re.paas.api.templating;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)

/**
 * This annotation is used to indicate that a field should not be passed into the velocity template
 * template at runtime
 */
public @interface Exclude {

}
