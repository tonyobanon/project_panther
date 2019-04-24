package com.re.paas.api.annotations.develop;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Prototype {
  String value() default "";
}
