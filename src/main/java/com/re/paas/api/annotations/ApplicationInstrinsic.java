package com.re.paas.api.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.re.paas.api.app_provisioning.AppClassLoader;

/**
 * This annotation specifies that a type should be defined inside {@link AppClassLoader}
 * instead of delegating to its parent class loader
 * 
 * @author anthonyanyanwu
 */
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface ApplicationInstrinsic {

}
