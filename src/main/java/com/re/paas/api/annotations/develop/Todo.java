package com.re.paas.api.annotations.develop;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(Todo.List.class)
public @interface Todo {
  String value() default "";

  @Retention(RetentionPolicy.RUNTIME)
  @interface List {
	  Todo[] value();
  }
}
