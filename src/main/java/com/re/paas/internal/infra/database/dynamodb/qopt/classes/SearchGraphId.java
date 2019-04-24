package com.re.paas.internal.infra.database.dynamodb.qopt.classes;

public class SearchGraphId {

	private String tableName;
	private String entryId;

	public SearchGraphId() {
	}

	public SearchGraphId(String tableName, String entryId) {
		this.tableName = tableName;
		this.entryId = entryId;
	}

	public SearchGraphId(String tableName, Integer entryId) {
		this.tableName = tableName;
		this.entryId = Integer.toString(entryId);
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getEntryId() {
		return entryId;
	}

	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}

	@Override
	public String toString() {
		return tableName + "/" + entryId;
	}

	public static SearchGraphId fromString(String value) {
		String arr[] = value.split("/");
		return new SearchGraphId(arr[0], arr[1]);
	}

}
