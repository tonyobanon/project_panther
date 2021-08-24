package com.re.paas.api.infra.database.model.exceptions;

public class TableNotFoundException extends RuntimeException {

	private final String tableName;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TableNotFoundException(String tableName) {
		this.tableName = tableName;
	}
	
	@Override
	public String getMessage() {
		return "Table: " + tableName + " was not found";
	}

}
