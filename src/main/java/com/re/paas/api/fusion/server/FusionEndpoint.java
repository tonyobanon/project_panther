package com.re.paas.api.fusion.server;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FusionEndpoint {
	
	String uri();

	boolean createXhrClient() default true;
	
	boolean requireSSL() default false;
	
	boolean isBlocking() default false;
	
	boolean isAsync() default true;
	
	String[] headerParams() default {};

	String[] requestParams() default {};

	String[] bodyParams() default {};
	
	boolean enableMultipart() default false;
	
	boolean cache() default false;
	
	HttpMethod method() default HttpMethod.GET;

	String functionality();
	
	String[] expectedVariablesExpression() default {};

	Class<? extends ServiceAuthenticator> customAuthenticator() default DefaultServiceAuthenticator.class;

	ServiceAffinity affinity() default ServiceAffinity.DISTRIBUTED;
	
}
