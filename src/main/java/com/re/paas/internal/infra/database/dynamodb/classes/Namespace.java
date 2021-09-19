package com.re.paas.internal.infra.database.dynamodb.classes;

public class Namespace {

	private final String name;

	private Namespace(String name) {
		this.name = name;
	}
	
	public static Namespace from(String tableName, String indexName) {
		return new Namespace(tableName + "/" + indexName);
	}
	
	public static Namespace from(String tableName) {
		return new Namespace(tableName);
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
