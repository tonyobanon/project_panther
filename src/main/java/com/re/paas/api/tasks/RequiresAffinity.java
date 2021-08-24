package com.re.paas.api.tasks;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(SOURCE)
@Target(METHOD)
/**
 * This annotation on a method means that it should be executed on every node in the cluster
 * if triggered
 * 
 * @author anthonyanyanwu
 */
public @interface RequiresAffinity {
 Affinity value();
}
