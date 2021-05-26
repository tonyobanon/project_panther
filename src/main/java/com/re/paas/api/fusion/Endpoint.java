package com.re.paas.api.fusion;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.re.paas.api.fusion.services.DefaultServiceAuthenticator;
import com.re.paas.api.fusion.services.ServiceAffinity;
import com.re.paas.api.fusion.services.ServiceAuthenticator;

@Retention(RetentionPolicy.RUNTIME)
public @interface Endpoint {
	
	String uri() default "";
	
	HttpMethod method() default HttpMethod.ALL;

	boolean requireSSL() default false;
	
	boolean isBlocking() default false;
	
	boolean isAsync() default true;
	
	String[] headerParams() default {};

	String[] requestParams() default {};

	String[] bodyParams() default {};
	
	boolean enableMultipart() default false;
	
	boolean cache() default false;
	
	Class<? extends ServiceAuthenticator> customAuthenticator() default DefaultServiceAuthenticator.class;

	ServiceAffinity affinity() default ServiceAffinity.DISTRIBUTED;
}