package com.re.paas.api.realms;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface RealmSpec {
	
	String parentRealm() default "";	
}
