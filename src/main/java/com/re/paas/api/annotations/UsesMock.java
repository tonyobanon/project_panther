package com.re.paas.api.annotations;

public @interface UsesMock {
  String value() default "";
}
