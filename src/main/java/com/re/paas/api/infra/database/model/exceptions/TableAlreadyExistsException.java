package com.re.paas.api.infra.database.model.exceptions;

public class TableAlreadyExistsException extends RuntimeException {

	private final String tableName;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TableAlreadyExistsException(String tableName) {
		this.tableName = tableName;
	}
	
	@Override
	public String getMessage() {
		return "Table: " + tableName + " was already exists";
	}

}
