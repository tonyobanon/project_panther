package com.re.paas.api.spi;

/**
 * SPI type classification define how delegates of such types consumes resources
 * discovered. However, it should be noted that at the barest minimum, delegates
 * are required to discover and expose resources that are delegated to them.
 * @see {@link TypeClassification#ACTIVE} {@code and} {@link TypeClassification#PASSIVE}
 * 
 * @author Tony
 */
public enum TypeClassification {

	/**
	 * These are types whose delegates actively executes code provided by the
	 * resource, at one point or another. All delegates for active types are
	 * required to be trusted.
	 */
	ACTIVE,

	/**
	 * These are types whose delegate do not actively run any code provided by the
	 * resource, at any point in time. Here, delegates are not required to be
	 * trusted
	 */
	PASSIVE
}
