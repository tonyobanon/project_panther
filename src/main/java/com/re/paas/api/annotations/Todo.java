package com.re.paas.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Repeatable(Todo.List.class)
public @interface Todo {
  String value() default "";

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE})
  @interface List {
	  Todo[] value();
  }
}
