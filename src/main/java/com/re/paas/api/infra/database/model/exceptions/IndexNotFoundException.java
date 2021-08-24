package com.re.paas.api.infra.database.model.exceptions;

public class IndexNotFoundException extends RuntimeException {

	private final String indexName;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public IndexNotFoundException(String indexName) {
		this.indexName = indexName;
	}
	
	public String getIndexName() {
		return indexName;
	}
	
	@Override
	public String getMessage() {
		return "Index: " + indexName + " was not found";
	}

}
