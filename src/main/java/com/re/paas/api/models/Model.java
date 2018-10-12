package com.re.paas.api.models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.re.paas.internal.models.ModelDelegate;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Model {
	
	boolean enabled() default true;

	String version() default ModelDelegate.DEFAULT_MODEL_VERSION;
	
	Class<? extends BaseModel>[] dependencies() default {};
	
}
