package com.re.paas.api.annotations.develop;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(BlockerTodo.List.class)
public @interface BlockerTodo {
  String value() default "";

  @Retention(RetentionPolicy.RUNTIME)
  @interface List {
	  BlockerTodo[] value();
  }
}
