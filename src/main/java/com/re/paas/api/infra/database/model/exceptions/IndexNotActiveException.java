package com.re.paas.api.infra.database.model.exceptions;

public class IndexNotActiveException extends RuntimeException {

	private final String indexName;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public IndexNotActiveException(String indexName) {
		this.indexName = indexName;
	}
	
	@Override
	public String getMessage() {
		return "Index: " + indexName + "is not ready";
	}

}
