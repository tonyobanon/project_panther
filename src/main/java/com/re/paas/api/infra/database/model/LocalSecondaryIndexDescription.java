package com.re.paas.api.infra.database.model;

public class LocalSecondaryIndexDescription extends IndexDefinition {

	public LocalSecondaryIndexDescription(String tableName, String indexName) {
		super(tableName, indexName, Type.LSI);
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
