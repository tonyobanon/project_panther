package com.re.paas.api.infra.database.model;

public class LocalSecondaryIndexDescription extends IndexDefinition {

	private Long itemCount;
	
	public LocalSecondaryIndexDescription(String tableName, String indexName) {
		super(tableName, indexName, Type.LSI);
	}

	public Long getItemCount() {
		return itemCount;
	}

	public LocalSecondaryIndexDescription setItemCount(Long itemCount) {
		this.itemCount = itemCount;
		return this;
	}
	
	/**
	 * <p>
	 * The total size of the specified index, in bytes.
	 * </p>
	 */
	private Long indexSizeBytes;

	public Long getIndexSizeBytes() {
		return indexSizeBytes;
	}

	public LocalSecondaryIndexDescription setIndexSizeBytes(Long indexSizeBytes) {
		this.indexSizeBytes = indexSizeBytes;
		return this;
	}
}
