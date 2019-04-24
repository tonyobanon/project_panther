package com.re.paas.api.infra.database.model;

public class IndexDescriptor {

	/**
	 * <p>
	 * The name of the table that this index resides in.
	 * </p>
	 */
	private String tableName;

	/**
	 * <p>
	 * The name of the index. The name must be unique among all other indexes on
	 * this table.
	 * </p>
	 */
	private final String indexName;
	
	private final Type type;
	

	public IndexDescriptor(String tableName, String indexName) {
		this(tableName, indexName, null);
	}
	
	public IndexDescriptor(String tableName, String indexName, Type type) {
		this.tableName = tableName;
		this.indexName = indexName;
		this.type = type;
	}

	/**
	 * <p>
	 * The name of the table.
	 * </p>
	 * 
	 * @param tableName The name of the table.
	 */

	public String getTableName() {
		return tableName;
	}
	
	public IndexDescriptor setTableName(String tableName) {
		this.tableName = tableName;
		return this;
	}

	/**
	 * <p>
	 * The name of the index. The name must be unique among all other indexes on
	 * this table.
	 * </p>
	 * 
	 * @return The name of the index. The name must be unique among all other
	 *         indexes on this table.
	 */

	public String getIndexName() {
		return this.indexName;
	}
	
	public Type getType() {
		return type;
	}

	public String getId() {
		String type = this.type != null ? this.type.name() + "-" : "";
		return getTableName() + "/" + type + getIndexName();
	}

	public static IndexDescriptor fromId(String indexId) {

		String[] arr = indexId.split("/");
		String[] indexName = arr[1].split("-");

		if(indexName.length > 1) {
			return new IndexDescriptor(arr[0], indexName[1], Type.valueOf(indexName[0]));
		} else {
			return new IndexDescriptor(arr[0], indexName[0]);
		}
	}
	
	public static enum Type {
		GSI, LSI
	}
}
