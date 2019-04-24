package com.re.paas.internal.infra.database;

public abstract class QueryThrottlePolicy {

	/**
	 * This is the maximum size that this database allows, before pagination is
	 * imposed
	 * 
	 * @return
	 */
	public abstract Integer maxResultSize();

	/**
	 * This is the extra overhead for storing collection, including sets, lists and
	 * maps
	 * 
	 * @return
	 */
	public abstract Integer collectionOverheadSize();

}
