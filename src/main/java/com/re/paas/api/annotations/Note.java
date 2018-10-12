package com.re.paas.api.annotations;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(SOURCE)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Note {
  String value() default "";
}
