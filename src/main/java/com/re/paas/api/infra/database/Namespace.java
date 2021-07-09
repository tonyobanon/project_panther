package com.re.paas.api.infra.database;

public class Namespace {

	private final String name;

	public Namespace(String name) {
		this.name = name;
	}
	
	public static Namespace from(String tableName, String indexName) {
		return new Namespace(tableName + "/" + indexName);
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
