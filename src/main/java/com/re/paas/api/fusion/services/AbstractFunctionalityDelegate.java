package com.re.paas.api.fusion.services;

import java.util.Collection;

import com.re.paas.api.spi.SpiDelegate;

public abstract class AbstractFunctionalityDelegate extends SpiDelegate<Functionality> {
	
	public static final String NAMESPACE_DELIMETER = "_";

	public abstract Functionality getFunctionality(String namespace, Integer contextId);

	/**
	 * The general contract is that the namespace
	 * and id should be concatenated and delimited by
	 * {@link AbstractFunctionalityDelegate.NAMESPACE_DELIMETER}
	 * 
	 * @param functionality
	 * @return
	 */
	public final String toString(Functionality functionality) {
		return toString(functionality.namespace(), functionality.id());
	}
	
	public abstract Functionality fromString(String functionalityString);
	
	public abstract Collection<Functionality> all();

	protected static String toString(String namespace, Integer id) {
		return namespace + NAMESPACE_DELIMETER + id;
	}

}
