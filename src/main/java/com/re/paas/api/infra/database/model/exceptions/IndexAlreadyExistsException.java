package com.re.paas.api.infra.database.model.exceptions;

public class IndexAlreadyExistsException extends RuntimeException {

	private final String indexName;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public IndexAlreadyExistsException(String indexName) {
		this.indexName = indexName;
	}
	
	public String getIndexName() {
		return indexName;
	}
	
	@Override
	public String getMessage() {
		return "Index: " + indexName + " already exists";
	}

}
